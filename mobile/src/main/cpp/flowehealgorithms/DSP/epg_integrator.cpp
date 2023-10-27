#pragma clang diagnostic push
#pragma ide diagnostic ignored "cppcoreguidelines-narrowing-conversions"
//
// Created by James Lin on 2023-09-24.
//
#include "epg_integrator.h"
#include <vector>
#include <cmath>


void calculate_slope_and_intercept(const std::vector<double>& data, double& m, double& b) {
    double sum_x = 0, sum_y = 0, sum_xy = 0, sum_x2 = 0;
    int n = data.size();

    for(int i = 0; i < n; ++i) {
        sum_x += i;
        sum_y += data[i];
        sum_xy += i * data[i];
        sum_x2 += i * i;
    }

    m = (n*sum_xy - sum_x*sum_y) / (n*sum_x2 - sum_x*sum_x);
    b = (sum_y - m*sum_x) / n;
}


double trapezoidal(const std::vector<double>& data, double dt, int start, int end, int step) {
    double result = 0.5 * (data[start] + data[end]);

    for (int i = start + step; i < end; i += step) {
        result += data[i];
    }

    return result * dt;
}

std::vector<double> integrate_gaussian(const std::vector<double>& data, double dt) {
    int n = data.size();
    std::vector<double> result(n, 0.0);

    // 這裡使用兩點高斯積分的權重和節點
    const double weights[2] = { 1.0, 1.0 };
    const double nodes[2] = { -1.0 / std::sqrt(3.0), 1.0 / std::sqrt(3.0) };

    for (int i = 1; i < n; ++i) {
        double midpoint = (data[i] + data[i - 1]) / 2.0;
        double half_interval = (data[i] - data[i - 1]) * dt / 2.0;
        for (int j = 0; j < 2; ++j) {
            result[i] += weights[j] * (midpoint + half_interval * nodes[j]);
        }
        result[i] *= dt;
        result[i] += result[i - 1];
    }

    return result;
}


std::vector<double> integrate_romberg(const std::vector<double>& data, double dt) {
    int n = data.size();
    if (n < 2) {
        return {};  // 数据过小，直接返回
    }

    const int MAX_ITER = std::min(5, n);  // 保证迭代次数不超过data的大小
    std::vector<std::vector<double>> R(MAX_ITER, std::vector<double>(MAX_ITER, 0.0));
    std::vector<double> result(n, 0.0);

    // 使用梯形法進行初步積分
    R[0][0] = 0.5 * dt * (data[0] + data[n-1]);
    for (int i = 1; i < n-1; ++i) {
        R[0][0] += dt * data[i];
    }

    result[0] = R[0][0] * dt;  // 初始值

    for (int k = 1; k < MAX_ITER; ++k) {
        double h = dt / std::pow(2, k);
        double sum = 0;

        int end = std::pow(2, k);
        for (int i = 1; i < end && i < n - 1; i += 2) {
            sum += data[i];
        }
        R[k][0] = 0.5 * R[k-1][0] + h * sum;

        for (int j = 1; j <= k; ++j) {
            R[k][j] = R[k][j-1] + (R[k][j-1] - R[k-1][j-1]) / (std::pow(4, j) - 1);
        }

        result[k] = R[k][k] * dt;
    }

    for(int i = MAX_ITER; i < n; i++) {
        result[i] = result[i-1] + data[i] * dt; // 繼續梯形法進行積分
    }

    return result;
}

std::vector<double> integrate_midpoint(const std::vector<double>& data, double dt) {
    int n = data.size();
    std::vector<double> result(n, 0.0);
    result[0] = data[0] * dt;

    for (int i = 1; i < n; ++i) {
        result[i] = result[i - 1] + data[i - 1] + (data[i] - data[i - 1]) / 2.0 * dt;
    }

    return result;
}


enum IntegrationMethod {
    Simpson,
    Trapezoidal,
    Romberg,
    Gaussaian,
    Midpoint
};

std::vector<double> integrate(const std::vector<double>& data, double dt, IntegrationMethod method) {
    std::vector<double> result(data.size(), 0.0);

    switch (method) {
        case Simpson:
            // 處理首項
            result[0] = 0.5 * data[0] * dt;

            // 處理中間項
            for (int i = 1; i < data.size() - 1; i += 2) {
                result[i] = result[i - 1] + (data[i - 1] + 4.0 * data[i] + data[i + 1]) * dt / 6.0;
                result[i + 1] = result[i] + (data[i] + 4.0 * data[i + 1] + data[i + 2]) * dt / 6.0;
            }

            // 處理尾項
            result[data.size() - 1] = result[data.size() - 2] + (data[data.size() - 2] + data[data.size() - 1]) * dt / 2.0;
            break;

        case Trapezoidal:
            // 處理首項
            result[0] = 0.5 * data[0] * dt;

            // 處理中間及尾項
            for (int i = 1; i < data.size(); ++i) {
                result[i] = result[i - 1] + (data[i - 1] + data[i]) * dt / 2.0;
            }
            break;

        case Romberg:
            result = integrate_romberg(data, dt);
            // 此处实现Romberg方法的代码。这种方法的实现需要递归和多级逼近，较为复杂。
            break;

        case Gaussaian:
            result = integrate_gaussian(data, dt);
            break;

        case Midpoint:
            result = integrate_midpoint(data, dt);
            break;
    }

    // 处理最后一个数据点
    if (method != Trapezoidal) {
        result[data.size() - 1] = result[data.size() - 2] + (data[data.size() - 2] + data[data.size() - 1]) * dt / 2.0;
    }

    return result;
}

void integrate_for_epg(const double* input, int input_size, double DC_bias, double Fs,
                         std::vector<double>& single_integration_result,
                         std::vector<double>& double_integration_result){

    double amplify_rate = 25.0;
    double dt = 1.0 / Fs;

    // 複製輸入數據到std::vector並扣除DC偏置
    std::vector<double> input_vector(input_size);
    for (int i = 0; i < input_size; ++i) {
        input_vector[i] = (input[i] - DC_bias)*amplify_rate;
    }
//    single_integration_result[0] = 0.5 * input_vector[0] * dt;
//    // 使用Simpson's Rule進行第一次積分
//    for (int i = 1; i < input_size - 1; i += 2) {
//        single_integration_result[i] = single_integration_result[i - 1] + (input_vector[i - 1] + 4.0 * input_vector[i] + input_vector[i + 1]) * dt / 6.0;
//        single_integration_result[i + 1] = single_integration_result[i] + (input_vector[i] + 4.0 * input_vector[i + 1] + input_vector[i + 2]) * dt / 6.0;
//    }
//    // last point of single_integration_result
//    single_integration_result[input_size - 1] = single_integration_result[input_size - 2] + (input_vector[input_size - 2] + input_vector[input_size - 1]) * dt / 2.0;

    single_integration_result = integrate(input_vector, dt, Romberg);
    // 進行線性回歸
    double m1, b1;
    calculate_slope_and_intercept(single_integration_result, m1, b1);

    // 校正第一次積分結果
    for(int i = 0; i < input_size; ++i) {
        single_integration_result[i] = single_integration_result[i] - (m1 * i + b1);
    }

//    double_integration_result[0] = 0;
//    // 使用Simpson's Rule進行第二次積分
//    for (int i = 1; i < input_size - 1; i += 2) {
//        double_integration_result[i] = double_integration_result[i - 1] + (single_integration_result[i - 1] + 4.0 * single_integration_result[i] + single_integration_result[i + 1]) * dt / 6.0;
//        double_integration_result[i + 1] = double_integration_result[i] + (single_integration_result[i] + 4.0 * single_integration_result[i + 1] + single_integration_result[i + 2]) * dt / 6.0;
//    }
//    // last point of double_integration_result
//    double_integration_result[input_size - 1] = double_integration_result[input_size - 2] + (single_integration_result[input_size - 2] + single_integration_result[input_size - 1]) * dt / 2.0;
    double_integration_result = integrate(single_integration_result, dt, Romberg);

    // 進行線性回歸
    double m2, b2;
    calculate_slope_and_intercept(double_integration_result, m2, b2);

    // 校正第二次積分結果
    for(int i = 0; i < input_size; ++i) {
        double_integration_result[i] = double_integration_result[i] - (m2 * i + b2);
    }

}

void calibrate_for_epg_integration(std::vector<double>& data_vector, const std::vector<int>& peaks_vector){
    unsigned int data_length = data_vector.size();
    unsigned int peaks_length = peaks_vector.size();
    double m, b;
    b = data_vector[peaks_vector[0]];
    for (int j = 0; j <peaks_vector[0]; ++j){
        data_vector[j] -= b;
    }
    unsigned int start, end;
    for (int i = 0; i < peaks_length - 1; ++i) {// peaks_length added 2
        start = peaks_vector[i];
        end = peaks_vector[i+1];
//        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "i: %d, start: %d, end: %d", i, start, end);

        // Step 3: Calculate slope and intercept for the segment
        m = (data_vector[end] - data_vector[start]) / (end - start);
        b = data_vector[start];
//        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "m: %f, b: %f", m, b);

        for (int j = 0; j < end - start; ++j) {
//            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "before data_vector[%d]: %f", start + j, data_vector[start + j]);
            data_vector[start + j] -= (m * j + b);
//            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "after data_vector[%d]: %f", start + j, data_vector[start + j]);
        }

    }
    //last segment use last m, data_vector[peaks_vector[last]]
    start = peaks_vector[peaks_length - 1];
    end = data_length - 1;
    b = data_vector[start];
    for (int j = 0; j <= end - start; ++j) {
        data_vector[start + j] -= (m * j + b);
    }
}

void differentiate_for_epg(const std::vector<double>& input, double Fs, std::vector<double>& output, double DC_bias) {
    int input_size = input.size();
    output.resize(input_size, 0.0); // Set size and initialize to 0.

    // Handle boundary conditions:
    output[0] = (input[1] - 2 * input[0] + input[0])* Fs * Fs;
    output[input_size - 1] = (input[input_size-1] - 2 * input[input_size-1] + input[input_size-2])* Fs * Fs;

    // Calculate the second derivative:
    for (int i = 1; i < input_size - 1; ++i) {
        output[i] = (input[i+1] - 2 * input[i] + input[i-1]) * Fs * Fs;
    }

    double amplify_rate = 25.0;

    // 複製輸入數據到std::vector並扣除DC偏置
    for (double & i : output) {
        i = (i/amplify_rate + DC_bias);
    }
}

#pragma clang diagnostic pop