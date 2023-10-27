#include "peak_detection.h"
#include <android/log.h>
#include <vector>
#include <complex>

#define LOG_TAG "GeniusPudding"

std::tuple<std::vector<int>, std::vector<int>, std::vector<float>, std::vector<float>, std::vector<float>>
find_peaks(float sample_rate,
           float drop_rate,
           float drop_rate_gain,
           float timer_init,
           float timer_peak_refractory_period,
           float peak_refinement_window,
           const std::string& Vpp_method,
           float Vpp_threshold,
           const std::vector<float>& data)
{
    std::vector<int> peaks_top;
    std::vector<int> peaks_bottom;
    float prev_sample_top = data[0];
    float prev_sample_bottom = data[0] * -1.0;
    bool rising_top = false;
    bool rising_bottom = false;
    float timer_top = timer_init;
    float timer_bottom = timer_init;
    float timer_peak_refractory_period_top = timer_peak_refractory_period;
    float timer_peak_refractory_period_bottom = timer_peak_refractory_period;
    float envelope_top = data[0];
    float envelope_bottom = data[0] * -1.0;
    std::vector<float> envelope_plot_top(1, envelope_top);
    std::vector<float> envelope_plot_bottom(1, envelope_bottom);
    bool Vpp_too_small = false;
    int peak_refinement_offset = static_cast<int>(round(peak_refinement_window * sample_rate));

    for (size_t i = 1 + peak_refinement_offset; i < data.size() - peak_refinement_offset; i++)
    {
        float curr_point_top = data[i];
        float curr_point_bottom = curr_point_top * -1.0;

        if (curr_point_top > prev_sample_top)
        {
            rising_top = true;
            envelope_top -= (timer_top <= 0) ? drop_rate * drop_rate_gain / sample_rate : drop_rate / sample_rate;
        }
        else if (curr_point_top < prev_sample_top && rising_top)
        {
            if (prev_sample_top >= envelope_top && !Vpp_too_small && timer_peak_refractory_period_top <= 0)
            {
                peaks_top.push_back(i - 1);
                rising_top = false;
                envelope_top = curr_point_top;
                envelope_plot_top.pop_back();
                envelope_plot_top.push_back(prev_sample_top);
                timer_top = timer_init;
                timer_peak_refractory_period_top = timer_peak_refractory_period;
            }
            else
            {
                envelope_top -= (timer_top <= 0) ? drop_rate * drop_rate_gain / sample_rate : drop_rate / sample_rate;
            }
        }
        else
        {
            envelope_top -= (timer_top <= 0) ? drop_rate * drop_rate_gain / sample_rate : drop_rate / sample_rate;
        }

        if (envelope_top < curr_point_top)
        {
            envelope_top = curr_point_top;
            rising_top = true;
        }

        prev_sample_top = curr_point_top;
        timer_top -= 1.0 / sample_rate;
        timer_peak_refractory_period_top -= 1.0 / sample_rate;
        envelope_plot_top.push_back(envelope_top);

        if (curr_point_bottom > prev_sample_bottom)
        {
            rising_bottom = true;
            envelope_bottom -= (timer_bottom <= 0) ? drop_rate * drop_rate_gain / sample_rate : drop_rate / sample_rate;
        }
        else if (curr_point_bottom < prev_sample_bottom && rising_bottom)
        {
            if (prev_sample_bottom >= envelope_bottom && !Vpp_too_small && timer_peak_refractory_period_bottom <= 0)
            {
                peaks_bottom.push_back(i - 1);
                rising_bottom = false;
                envelope_bottom = curr_point_bottom;
                envelope_plot_bottom.pop_back();
                envelope_plot_bottom.push_back(prev_sample_bottom);
                timer_bottom = timer_init;
                timer_peak_refractory_period_bottom = timer_peak_refractory_period;
            }
            else
            {
                envelope_bottom -= (timer_bottom <= 0) ? drop_rate * drop_rate_gain / sample_rate : drop_rate / sample_rate;
            }
        }
        else
        {
            envelope_bottom -= (timer_bottom <= 0) ? drop_rate * drop_rate_gain / sample_rate : drop_rate / sample_rate;
        }

        if (envelope_bottom < curr_point_bottom)
        {
            envelope_bottom = curr_point_bottom;
            rising_bottom = true;
        }

        prev_sample_bottom = curr_point_bottom;
        timer_bottom -= 1.0 / sample_rate;
        timer_peak_refractory_period_bottom -= 1.0 / sample_rate;
        envelope_plot_bottom.push_back(envelope_bottom);

        float Vpp = envelope_top - envelope_bottom * -1;
        Vpp_too_small = Vpp <= Vpp_threshold;
    }

    std::transform(envelope_plot_bottom.begin(), envelope_plot_bottom.end(), envelope_plot_bottom.begin(), std::negate<float>());
    std::vector<float> Vpp_plot;

    if (Vpp_method == "continuous")
    {
        std::transform(envelope_plot_top.begin(), envelope_plot_top.end(), envelope_plot_bottom.begin(), std::back_inserter(Vpp_plot), std::minus<float>());
    }
    else if (Vpp_method == "on_peak")
    {
        float curr_top = envelope_plot_top[0];
        float curr_bottom = envelope_plot_bottom[0];
        for (size_t i = 0; i < envelope_plot_top.size(); i++)
        {
            if (std::find(peaks_top.begin(), peaks_top.end(), i) != peaks_top.end())
                curr_top = envelope_plot_top[i];
            if (std::find(peaks_bottom.begin(), peaks_bottom.end(), i) != peaks_bottom.end())
                curr_bottom = envelope_plot_bottom[i];
            Vpp_plot.push_back(curr_top - curr_bottom);
        }
    }
    else
    {
        std::transform(envelope_plot_top.begin(), envelope_plot_top.end(), envelope_plot_bottom.begin(), std::back_inserter(Vpp_plot), std::minus<float>());
    }

    std::vector<float> zero_pad(peak_refinement_offset, 0.0);
    envelope_plot_top.insert(envelope_plot_top.begin(), zero_pad.begin(), zero_pad.end());
    envelope_plot_bottom.insert(envelope_plot_bottom.begin(), zero_pad.begin(), zero_pad.end());

    return std::make_tuple(peaks_top, peaks_bottom, envelope_plot_top, envelope_plot_bottom, Vpp_plot);
}

std::vector<int> local_maxima_1d(const std::vector<double>& x) {
    std::vector<int> midpoints, left_edges, right_edges;
    int i = 1, i_ahead = 0, i_max = x.size() - 1, m = 0;

    while (i < i_max) {
        if (x[i - 1] < x[i]) {
            i_ahead = i + 1;

            while (i_ahead < i_max && x[i_ahead] == x[i]) {
                i_ahead++;
            }

            if (x[i_ahead] < x[i]) {
                left_edges.push_back(i);
                right_edges.push_back(i_ahead - 1);
                midpoints.push_back((left_edges[m] + right_edges[m]) / 2);
                m++;
                i = i_ahead;
            }
        }
        i++;
    }

    return midpoints;
}

std::vector<int> local_minima_1d(const std::vector<double>& x) {
    std::vector<int> midpoints, left_edges, right_edges;
    int i = 1, i_ahead = 0, i_max = x.size() - 1, m = 0;

    while (i < i_max) {
        if (x[i - 1] > x[i]) {
            i_ahead = i + 1;

            while (i_ahead < i_max && x[i_ahead] == x[i]) {
                i_ahead++;
            }

            if (x[i_ahead] > x[i]) {
                left_edges.push_back(i);
                right_edges.push_back(i_ahead - 1);
                midpoints.push_back((left_edges[m] + right_edges[m]) / 2);
                m++;
                i = i_ahead;
            }
        }
        i++;
    }

    return midpoints;
}

std::vector<std::vector<std::pair<int, int>>> find_epg_points(std::vector<double> input_data, std::vector<int> peaks, std::vector<int> troughs){
    // Create a list to hold the List of pairs of indices for each waveform
    std::vector<std::vector<std::pair<int, int>>> results;
    int peaks_num = peaks.size();
    int troughs_num = troughs.size();
    unsigned int length = input_data.size();

    if(abs(peaks_num - troughs_num) > 1) {
        // 錯誤處理: peaks和troughs的長度差異大於1，直接返回null
        return {};
    }

    // Check if peaks and troughs are interleaved, or the input is non-sense
    if(!check_is_interleaved(peaks, troughs)) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "The peaks and troughs are not interleaved.");
        // ... release array elements and return nullptr
        return {};
    }


    for (int i = 0 ; i < peaks_num-1; ++i){
//        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "peaks[%d]: %d", i, peaks[i]);
        if (peaks[i] >= length-1) {
            // Handle the error appropriately
            //return empty vector
            return {};
        }
        // waveform is from input_data[peakIdx[i]] to input_data[peakIdx2[i]]
        if(peaks[i] >= troughs[i] && i+1 >= troughs_num) {
            // Handle the error appropriately
            return {};
        }
        int trough = peaks[i] < troughs[i] ? troughs[i] : troughs[i + 1];
//        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "trough: %d, peaks[%d]:%d, troughs[%d]:%d, troughs[%d]:%d", trough, i+1, peaks[i+1], i, troughs[i], i+1, troughs[i+1]);
        std::vector<double> waveform(input_data.begin() + trough,
                                     input_data.begin() + peaks[i+1]);

//        std::vector<double> smoothed_waveform = savgol_filter(waveform, 5, 2);
//        logVector(waveform, "waveform: ");
//        logVector(smoothed_waveform, "smoothed_waveform: ");

        // Find the peaks and troughs in the smoothed_waveform
        int offset = trough - peaks[i];
//        std::vector<std::pair<int, int>> peaks_and_troughs = find_peaks_and_troughs(waveform, offset);
        // 查找局部最大值和最小值
        std::vector<int> local_max_indices = local_maxima_1d(waveform);
        std::vector<int> local_min_indices = local_minima_1d(waveform);
//        logVector(local_max_indices, "local_max_indices: ");
//        logVector(local_min_indices, "local_min_indices: ");

        std::vector<std::pair<int, int>> peaks_and_troughs;
        for (size_t j = 0; j < local_max_indices.size() && j < local_min_indices.size(); ++j) {
            if (local_max_indices[j] >= local_min_indices[j]) {
//                __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "reversed local_max_indices[%d]: %d, local_min_indices[%d]: %d", j, local_max_indices[j], j, local_min_indices[j]);
                continue;
            }
            peaks_and_troughs.emplace_back(local_max_indices[j] + offset, local_min_indices[j] + offset);
            if (peaks_and_troughs.size() >= 2) {
                break;
            }
        }

//        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "entries of peaks_and_troughs: %d", peaks_and_troughs.size());
        //__android_log_print the content of peaks_and_troughs
        // Store the first two pairs of peak and trough
        results.push_back(peaks_and_troughs);
    }
    return results;
}


// not good enough
std::vector<std::pair<int, int>> find_peaks_and_troughs(const std::vector<double>& waveform, int offset = 0) {
    std::vector<std::pair<int, int>> peaks_and_troughs;
    int size = waveform.size();
    if(size < 7) {
        return peaks_and_troughs;
    }
    int last_peak = -1, last_trough = -1;
    for(int i = 3; i < size - 4; ++i) {
        // Find a peak

        if(waveform[i] > waveform[i-1] && waveform[i] > waveform[i+1]) {
            last_peak = i+offset;
        }
            // Find a trough
        else if(waveform[i] < waveform[i-1] && waveform[i] < waveform[i+1]) {
            last_trough = i+offset;
        }

        if (last_peak != -1 && last_trough != -1) {
            peaks_and_troughs.emplace_back(last_peak, last_trough);
            if(peaks_and_troughs.size() >= 2){
                return peaks_and_troughs;
            }
            last_peak = -1;
            last_trough = -1;
        }
    }
    return peaks_and_troughs;
}

//boolean function for Check if peaks and troughs are interleaved
bool check_is_interleaved(const std::vector<int>& peaks, const std::vector<int>& troughs) {
    int peaks_num = peaks.size();
    int troughs_num = troughs.size();

    std::vector<std::pair<int, std::string>> combinedIndices;
    combinedIndices.reserve(peaks_num + troughs_num);

    for(int i = 0; i < peaks_num; i++) {
        combinedIndices.emplace_back(peaks[i], "peakIdx");
    }

    for(int i = 0; i < troughs_num; i++) {
        combinedIndices.emplace_back(troughs[i], "peakIdx2");
    }

    std::sort(combinedIndices.begin(), combinedIndices.end(),
              [](const std::pair<int, std::string>& a, const std::pair<int, std::string>& b) {
                  return a.first < b.first;
              });

    for(size_t i = 1; i < combinedIndices.size(); i++) {
        if(combinedIndices[i-1].second == combinedIndices[i].second) {
            return false;
        }
    }
    return true;
}