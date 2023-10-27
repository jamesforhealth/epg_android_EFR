#include "basic_charts.h"
#include <android/log.h>
#define LOG_TAG "GeniusPudding"
// void print_vector(const std::vector<double> &vec)
// {
//     for (const auto &val : vec)
//     {
//         __android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "%f", val);
//     }
// }

template <typename T>
std::vector<double> calculate_diff(const std::vector<T> &input, double sample_rate = 100.0)
{
    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Start calculate_diff");
    if (input.size() < 2)
    {
        return {};
    }

    std::vector<double> result(input.size());
    std::adjacent_difference(input.begin(), input.end(), result.begin());

    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculate_diff result[0]: %f, result[1]: %f, input[0]: %f, input[1]: %f", result[0], result[1], input[0], input[1]);
    if (!result.empty())
        result.erase(result.begin());
    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculate_diff");
    return result;
}

std::tuple<std::vector<double>, std::vector<double>> calculateHeartRate(int dataLen, const std::vector<int> &peak_indices, double sample_rate)
{
    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Start calculateHeartRate");

    std::vector<double> HR_instantaneous_X;
    std::vector<double> HR_instantaneous_Y;

    if (peak_indices.size() >= 2)
    {
        // Generate x_raw
        std::vector<double> x_raw(dataLen);
        for (int i = 0; i < dataLen; ++i)
        {
            x_raw[i] = static_cast<double>(i) / sample_rate;
        }
        // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "datalen: %d", dataLen);

        // Extract elements from x_raw according to peak_indices
        HR_instantaneous_X.resize(peak_indices.size());
        for (int i = 0; i < peak_indices.size(); ++i)
        {
            // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "peak_indices[%d]: %d", i, peak_indices[i]);
            HR_instantaneous_X[i] = x_raw[peak_indices[i]];
        }

        // Calculate HR_instantaneous_Y
        HR_instantaneous_Y = calculate_diff(HR_instantaneous_X, sample_rate);

        for (double &val : HR_instantaneous_Y)
        {
            if (val == 0)
            {
                val = 300; // 300 bpm
            }
            else
            {
                val = 60 / val;
            }
        }
        // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "HR_instantaneous_X.size(): %d, HR_instantaneous_Y.size(): %d", HR_instantaneous_X.size(), HR_instantaneous_Y.size());
        // Make sure the sizes of HR_instantaneous_X and HR_instantaneous_Y are the same
        if (HR_instantaneous_X.size() > HR_instantaneous_Y.size())
            HR_instantaneous_X.pop_back();

    }

    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculateHeartRate");
    return {HR_instantaneous_X, HR_instantaneous_Y};
}

std::tuple<std::vector<double>, std::vector<double>> calculatePoincarePlot(const std::vector<int> &peak_indices, double sample_rate)
{
    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Start calculatePoincarePlot");

    if (peak_indices.size() < 3)
    {
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculatePoincarePlot empty");
        return {{}, {}};
    }

    std::vector<double> Tpp = calculate_diff(peak_indices, sample_rate);
    std::transform(Tpp.begin(), Tpp.end(), Tpp.begin(), [sample_rate](double n)
                   { return n / sample_rate; });
    if (Tpp.size() < 2)
    {
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculatePoincarePlot empty");
        return {{}, {}};
    }
    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculatePoincarePlot");
    return {std::vector<double>(Tpp.begin(), Tpp.end() - 2), std::vector<double>(Tpp.begin() + 1, Tpp.end() - 1)};
}

std::tuple<std::vector<double>, std::vector<std::vector<double>>> calculateEyeDiagram(const std::vector<double> &data_points, const std::vector<int> &peak_indices, double sample_rate)
{
    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Start calculateEyeDiagram");
    if (peak_indices.size() < 3)
    {
        // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculateEyeDiagram empty");
        return {{}, {}};
    }
    const double T_eye_pre_peak = 0.5;  // seconds
    const double T_eye_post_peak = 1.5; // seconds
    std::vector<std::vector<double>> eye_diagram_data_Y;

    for (const auto &idx_peak : peak_indices)
    {
        int pre_calc = idx_peak - static_cast<int>(std::round(T_eye_pre_peak * sample_rate));
        int post_calc = idx_peak + static_cast<int>(std::round(T_eye_post_peak * sample_rate));

        if (pre_calc < 0 || post_calc > static_cast<int>(data_points.size()))
        {
            continue;
        }

        std::vector<double> eye_trace(post_calc - pre_calc);
        std::copy(data_points.begin() + pre_calc, data_points.begin() + post_calc, eye_trace.begin());
        eye_diagram_data_Y.push_back(eye_trace);
    }
    std::vector<double> eye_diagram_data_X(static_cast<int>(std::round(T_eye_pre_peak * sample_rate + T_eye_post_peak * sample_rate)));
    std::iota(eye_diagram_data_X.begin(), eye_diagram_data_X.end(), -static_cast<int>(std::round(T_eye_pre_peak * sample_rate)));
    std::transform(eye_diagram_data_X.begin(), eye_diagram_data_X.end(), eye_diagram_data_X.begin(), [sample_rate](double n)
                   { return n / sample_rate; });
    // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "End calculateEyeDiagram");
    return {eye_diagram_data_X, eye_diagram_data_Y};
}


