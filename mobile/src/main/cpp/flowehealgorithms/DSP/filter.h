//
// Created by James Lin on 2023-09-20.
//
#ifndef FILTER_H
#define FILTER_H
#include <vector>
std::vector<double> savgol_filter(const std::vector<double>& input, int window_length, int polyorder);

#endif // FILTER_H