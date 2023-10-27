// basic_charts.h
#ifndef BASIC_CHARTS_H
#define BASIC_CHARTS_H

#include <vector>
#include <tuple>
#include <algorithm>
#include <cmath>
#include <numeric>
// Calculate heart rate from peak indices and sample rate
std::tuple<std::vector<double>, std::vector<double>> calculateHeartRate(int dataLen, const std::vector<int> &peak_indices, double sample_rate);

// Calculate Poincare plot from peak indices
std::tuple<std::vector<double>, std::vector<double>> calculatePoincarePlot(const std::vector<int> &peak_indices, double sample_rate);

// Calculate eye diagram from data points, peak indices, and sample rate
std::tuple<std::vector<double>, std::vector<std::vector<double>>> calculateEyeDiagram(const std::vector<double> &data_points, const std::vector<int> &peak_indices, double sample_rate);

#endif // BASIC_CHARTS_H