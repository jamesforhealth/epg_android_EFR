//
// Created by James Lin on 2023-09-20.
//
#include <vector>
#include <Eigen/Dense>
#include "filter.h"

std::vector<double> savgol_filter(const std::vector<double>& input, int window_length, int polyorder) {
    if (window_length % 2 == 0) {
        // Window length must be odd, increment it by 1
        window_length += 1;
    }

    int half_window = window_length / 2;
    Eigen::MatrixXd A(window_length, polyorder + 1);
    for (int i = -half_window; i <= half_window; ++i) {
        for (int j = 0; j <= polyorder; ++j) {
            A(i + half_window, j) = std::pow(i, j);
        }
    }

    Eigen::VectorXd y(window_length);
    Eigen::MatrixXd ATA = A.transpose() * A;
    Eigen::MatrixXd ATA_inv = ATA.inverse();
    Eigen::MatrixXd coeffs = ATA_inv * A.transpose();

    std::vector<double> output(input.size(), 0.0);
    for (size_t i = half_window; i < input.size() - half_window; ++i) {
        for (int j = -half_window; j <= half_window; ++j) {
            output[i] += coeffs(j + half_window, 0) * input[i + j];
        }
    }
    return output;
}