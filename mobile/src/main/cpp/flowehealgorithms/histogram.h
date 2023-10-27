//
// Created by James Lin on 2023.09.26.
//

#ifndef HISTOGRAM_HISTOGRAM_H
#define HISTOGRAM_HISTOGRAM_H

#include <vector>
#include <map>
#include <iostream>
#include <limits>

std::map<double, int> histogram(const std::vector<double>& data, const std::vector<double>& bin_starts);


#endif //HISTOGRAM_HISTOGRAM_H