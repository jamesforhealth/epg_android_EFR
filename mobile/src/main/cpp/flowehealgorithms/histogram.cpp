//
// Created by James Lin on 2023.09.26.
//

#include "histogram.h"

std::map<double, int> histogram(const std::vector<double>& data,
                                const std::vector<double>& bin_starts) {

    std::map<double, int> result;

    for (const auto& start : bin_starts) {
        result[start] = 0;
    }

    for (const auto& num : data) {
        for (auto it = bin_starts.rbegin(); it != bin_starts.rend(); ++it) {
            if (num >= *it) {
                result[*it]++;
                break;
            }
        }
    }

    return result;
}