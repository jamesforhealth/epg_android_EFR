#ifndef PEAK_DETECTION_H
#define PEAK_DETECTION_H
#include <cmath>
#include <vector>
#include <tuple>
#include <algorithm>
#include <functional>
#include <string>
#ifdef __cplusplus
extern "C" {
#endif
std::tuple<std::vector<int>, std::vector<int>, std::vector<float>, std::vector<float>, std::vector<float>>
find_peaks(float sample_rate,
           float drop_rate,
           float drop_rate_gain,
           float timer_init,
           float timer_peak_refractory_period,
           float peak_refinement_window,
           const std::string& Vpp_method,
           float Vpp_threshold,
           const std::vector<float>& data);
std::vector<int> local_maxima_1d(const std::vector<double>& x);
std::vector<int> local_minima_1d(const std::vector<double>& x);
std::vector<std::vector<std::pair<int, int>>> find_epg_points(std::vector<double> input_data, std::vector<int> peaks, std::vector<int> troughs);
std::vector<std::pair<int, int>> find_peaks_and_troughs(const std::vector<double>& waveform, int offset);//deprecated
bool check_is_interleaved(const std::vector<int>& peaks, const std::vector<int>& troughs);
#ifdef __cplusplus
}
#endif
#endif // PEAK_DETECTION_H
