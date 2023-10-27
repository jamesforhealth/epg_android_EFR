//
// Created by James Lin on 2023-09-24.
//
#ifndef EPG_INTEGRATOR_H
#define EPG_INTEGRATOR_H


#include <vector>
#ifdef __cplusplus
extern "C" {
#endif
void calculate_slope_and_intercept(const std::vector<double>& data, double& m, double& b);
void integrate_for_epg(const double* input, int input_size, double DC_bias, double Fs,
                       std::vector<double>& single_integration_result,
                       std::vector<double>& double_integration_result);
void calibrate_for_epg_integration(std::vector<double>& data_vector, const std::vector<int>& peaks_vector);
void differentiate_for_epg(const std::vector<double>& input, double dt, std::vector<double>& output, double DC_bias);
#ifdef __cplusplus
}
#endif
#endif // EPG_INTEGRATOR_H