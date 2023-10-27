#include <jni.h>
#include <string>
#include <vector>
#include <complex>
#include <android/log.h>
#include "flowehealgorithms/flowehealgorithms.h"
#include "flowehealgorithms/DSP/epg_integrator.h"
#include "pffft/pffft.h"
//#include <ceres/ceres.h>

#define LOG_TAG "GeniusPudding"

typedef std::complex<double> Complex;
const double PI = 3.14159265;
void logVector(const std::vector<double>& vec, const char* prefix = "") {
    std::ostringstream oss;
    oss << prefix;
    oss << "[";
    for(size_t i = 0; i < vec.size(); ++i) {
        oss << vec[i];
        if(i != vec.size() - 1) {
            oss << ", ";
        }
    }
    oss << "]";

    std::string vecString = oss.str();
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s", vecString.c_str());
}

void logVector(const std::vector<int>& vec, const char* prefix = "") {
    std::ostringstream oss;
    oss << prefix;
    oss << "[";
    for(size_t i = 0; i < vec.size(); ++i) {
        oss << vec[i];
        if(i != vec.size() - 1) {
            oss << ", ";
        }
    }
    oss << "]";

    std::string vecString = oss.str();
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s", vecString.c_str());
}

jobject vectorToJList(JNIEnv *env, const std::vector<double> &vec)
{
    jclass listClass = env->FindClass("java/util/ArrayList");
    jmethodID listConstructor = env->GetMethodID(listClass, "<init>", "()V");
    jobject jList = env->NewObject(listClass, listConstructor);
    jmethodID listAdd = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

    jclass doubleClass = env->FindClass("java/lang/Double");
    jmethodID doubleConstructor = env->GetMethodID(doubleClass, "<init>", "(D)V");

    for (double val : vec)
    {
        jobject jDouble = env->NewObject(doubleClass, doubleConstructor, val);
        env->CallBooleanMethod(jList, listAdd, jDouble);
        env->DeleteLocalRef(jDouble); // Avoid memory leak
    }

    return jList;
}
jobject vector2DToJList(JNIEnv *env, const std::vector<std::vector<double>> &vec)
{
    jclass listClass = env->FindClass("java/util/ArrayList");
    jmethodID listConstructor = env->GetMethodID(listClass, "<init>", "()V");
    jmethodID listAdd = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

    jobject jList2D = env->NewObject(listClass, listConstructor);

    for (const auto &innerVec : vec)
    {
        jobject jList = vectorToJList(env, innerVec);
        env->CallBooleanMethod(jList2D, listAdd, jList);
        env->DeleteLocalRef(jList); // Avoid memory leak
    }

    return jList2D;
}
extern "C" JNIEXPORT jobject JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calculateHistogramFromJNI(JNIEnv *env, jobject obj, jdoubleArray jData, jdoubleArray jBinStart) {
    // Convert jdoubleArray to std::vector<double>
    jdouble *dataElements = env->GetDoubleArrayElements(jData, nullptr);
    jsize dataLength = env->GetArrayLength(jData);
    std::vector<double> data(dataElements, dataElements + dataLength);
    env->ReleaseDoubleArrayElements(jData, dataElements, JNI_ABORT);

    jdouble *binStartElements = env->GetDoubleArrayElements(jBinStart, nullptr);
    jsize binStartLength = env->GetArrayLength(jBinStart);
    std::vector<double> binStart(binStartElements, binStartElements + binStartLength);
    env->ReleaseDoubleArrayElements(jBinStart, binStartElements, JNI_ABORT);

    // Call the histogram method
    std::map<double, int> result_map = histogram(data, binStart);

    // Create the HashMap object
    jclass hashMapClass = env->FindClass("java/util/HashMap");
    jmethodID hashMapInitID = env->GetMethodID(hashMapClass, "<init>", "()V");
    jobject hashMapObj = env->NewObject(hashMapClass, hashMapInitID);

    // Create Double and Integer objects and put data into HashMap
    jmethodID putMethod = env->GetMethodID(hashMapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    jclass doubleClass = env->FindClass("java/lang/Double");
    jmethodID doubleInit = env->GetMethodID(doubleClass, "<init>", "(D)V");
    jclass integerClass = env->FindClass("java/lang/Integer");
    jmethodID integerInit = env->GetMethodID(integerClass, "<init>", "(I)V");

    for (auto &pair : result_map) {
        jobject keyObj = env->NewObject(doubleClass, doubleInit, pair.first);
        jobject valueObj = env->NewObject(integerClass, integerInit, pair.second);

        env->CallObjectMethod(hashMapObj, putMethod, keyObj, valueObj);
    }

    return hashMapObj;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_pointDetectionFromJNI(
        JNIEnv* env,
        jobject thiz,
        jdoubleArray raw_data_j,
        jintArray peakIdx,
        jintArray peakIdx2
        ){
    jsize length = env->GetArrayLength(raw_data_j);
    jdouble* input = env->GetDoubleArrayElements(raw_data_j, nullptr);
    std::vector<double> input_data(input, input+length);
    //smoothing the data between the adjacent peak
    jsize peaks_num = env->GetArrayLength(peakIdx);
    jint *peak_idx = env->GetIntArrayElements(peakIdx, nullptr);
    std::vector<int> peaks(peak_idx, peak_idx + peaks_num);

    jsize troughs_num = env->GetArrayLength(peakIdx2);
    jint *peak_idx2 = env->GetIntArrayElements(peakIdx2, nullptr);
    std::vector<int> troughs(peak_idx2, peak_idx2 + troughs_num);

    std::vector<std::vector<std::pair<int, int>>> results = find_epg_points(input_data, peaks, troughs);

    // Find the PeakDetectionResult class and its constructor
    jclass resultClass = env->FindClass("com/flowehealth/internal/models/datastructs/PointDetectionResult");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(Ljava/util/List;)V");

    // Convert the results vector to a Java List of List of Pairs
    jclass listClass = env->FindClass("java/util/ArrayList");
    jmethodID listConstructor = env->GetMethodID(listClass, "<init>", "()V");
    jmethodID listAddMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");
    jclass pairClass = env->FindClass("kotlin/Pair");
    jmethodID pairConstructor = env->GetMethodID(pairClass, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");

    jobject resultsList = env->NewObject(listClass, listConstructor);

    jclass integerClass = env->FindClass("java/lang/Integer");
    jmethodID integerConstructor = env->GetMethodID(integerClass, "<init>", "(I)V");

    for(const auto& waveform_result : results) {
        jobject waveformList = env->NewObject(listClass, listConstructor);

        for(const auto& peak_and_trough : waveform_result) {
            jobject peak = env->NewObject(pairClass, pairConstructor, env->NewObject(integerClass, integerConstructor, peak_and_trough.first), env->NewObject(integerClass, integerConstructor, peak_and_trough.second));
            env->CallBooleanMethod(waveformList, listAddMethod, peak);
        }

        env->CallBooleanMethod(resultsList, listAddMethod, waveformList);
    }

    // Create a new object of PeakDetectionResult and return
    jobject result = env->NewObject(resultClass, constructor, resultsList);

    // Release the arrays
    env->ReleaseDoubleArrayElements(raw_data_j, input, JNI_ABORT);
    env->ReleaseIntArrayElements(peakIdx, peak_idx, JNI_ABORT);
    env->ReleaseIntArrayElements(peakIdx2, peak_idx2, JNI_ABORT);

    return result;
}


extern "C" JNIEXPORT jobject JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_peakDetectionFromJNI(
    JNIEnv *env,
    jobject thiz,
    jdoubleArray raw_data_j,
    jdouble Fs,
    jdouble drop_rate,
    jdouble drop_rate_gain,
    jdouble timer_init,
    jdouble timer_peak_refractory_period,
    jdouble peak_refinement_window)
{
    // Step 1: Convert Java double array to C++ vector
    jsize len = env->GetArrayLength(raw_data_j);
    jdouble *raw_data_p = env->GetDoubleArrayElements(raw_data_j, nullptr);
    std::vector<float> raw_data(raw_data_p, raw_data_p + len);

    // Step 2: Call find_peaks function
//    PeakDetector detector(Fs, float(drop_rate), float(drop_rate_gain), float(timer_init), float(timer_peak_refractory_period), float(peak_refinement_window), "on_peak", 0.1);
//    auto [peaks_top, peaks_bottom, envelope_plot_top, envelope_plot_bottom, Vpp_plot] = detector.
    auto [peaks_top, peaks_bottom, envelope_plot_top, envelope_plot_bottom, Vpp_plot] = find_peaks(Fs, float(drop_rate), float(drop_rate_gain), float(timer_init), float(timer_peak_refractory_period), float(peak_refinement_window), "on_peak", 0.1,raw_data);

    // Step 3: Convert the results to Java array and pack them into a Java object

    // Convert each std::vector<int> to jintArray
    jintArray peakIdx = env->NewIntArray(peaks_top.size());
    env->SetIntArrayRegion(peakIdx, 0, peaks_top.size(), peaks_top.data());

    jintArray peakIdx2 = env->NewIntArray(peaks_bottom.size());
    env->SetIntArrayRegion(peakIdx2, 0, peaks_bottom.size(), peaks_bottom.data());

    // Convert each std::vector<float> to jdoubleArray
    std::vector<double> envelope_plot_top_double(envelope_plot_top.begin(), envelope_plot_top.end());
    jdoubleArray envelope = env->NewDoubleArray(envelope_plot_top_double.size());
    env->SetDoubleArrayRegion(envelope, 0, envelope_plot_top_double.size(), envelope_plot_top_double.data());

    std::vector<double> envelope_plot_bottom_double(envelope_plot_bottom.begin(), envelope_plot_bottom.end());
    jdoubleArray envelope2 = env->NewDoubleArray(envelope_plot_bottom_double.size());
    env->SetDoubleArrayRegion(envelope2, 0, envelope_plot_bottom_double.size(), envelope_plot_bottom_double.data());

    std::vector<double> Vpp_plot_double(Vpp_plot.begin(), Vpp_plot.end());
    jdoubleArray vpp = env->NewDoubleArray(Vpp_plot_double.size());
    env->SetDoubleArrayRegion(vpp, 0, Vpp_plot_double.size(), Vpp_plot_double.data());

    // Find the PeakDetectionResult class and its constructor
    jclass resultClass = env->FindClass("com/flowehealth/internal/models/datastructs/PeakDetectionResult");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "([I[I[D[D[D)V");

    // Create a new object of PeakDetectionResult and return
    jobject result = env->NewObject(resultClass, constructor, peakIdx, peakIdx2, envelope, envelope2, vpp);

    // Release the Java double array elements
    env->ReleaseDoubleArrayElements(raw_data_j, raw_data_p, JNI_ABORT);

    return result;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calculateEyeDiagramFromJNI(
    JNIEnv *env,
    jobject thiz,
    jdoubleArray raw_data_j,
    jintArray peak_idx_j,
    jdouble Fs)
{
    // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Start calculateEyeDiagram");
    // Convert Java double array to C++ vector
    jsize len_r = env->GetArrayLength(raw_data_j);
    jdouble *raw_data_p = env->GetDoubleArrayElements(raw_data_j, nullptr);
    std::vector<double> raw_data(raw_data_p, raw_data_p + len_r);

    jsize len_pk = env->GetArrayLength(peak_idx_j);
    jint *peak_idx_p = env->GetIntArrayElements(peak_idx_j, nullptr);
    std::vector<int> peak_idx(peak_idx_p, peak_idx_p + len_pk);

    // Calculate eye diagram

    std::vector<double> x;
    std::vector<std::vector<double>> y;
    try
    {
        std::tie(x, y) = calculateEyeDiagram(raw_data, peak_idx, Fs);
    }
    catch (const std::logic_error &e)
    {
        // 處理異常的代碼...
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught logic_error: %s", e.what());
    }
    catch (const std::runtime_error &e)
    {
        // 處理異常的代碼...
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught runtime_error: %s", e.what());
    }
    catch (std::exception &e)
    {
        // Other exceptions
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught exception: %s", e.what());
    }

    // Convert C++ vectors to Java arrays
    jobject x_j = vectorToJList(env, x);
    jobject y_j = vector2DToJList(env, y);

    // Find the EyeDiagramData class and its constructor
    jclass resultClass = env->FindClass("com/flowehealth/internal/models/datastructs/EyeDiagramData");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(Ljava/util/List;Ljava/util/List;)V");

    // Create a new object of EyeDiagramData and return
    jobject result = env->NewObject(resultClass, constructor, x_j, y_j);

    // Release the Java double array elements
    env->ReleaseDoubleArrayElements(raw_data_j, raw_data_p, JNI_ABORT);
    env->ReleaseIntArrayElements(peak_idx_j, peak_idx_p, JNI_ABORT);
    // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "End calculateEyeDiagram");
    return result;
}
extern "C" JNIEXPORT jobject JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calculateHeartRateFromJNI(
    JNIEnv *env,
    jobject thiz,
    jint dataLen,
    jintArray peak_idx_j,
    jdouble Fs)
{
    // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Start calculateHeartRate");
    // Convert Java double array to C++ vector
    jsize len = env->GetArrayLength(peak_idx_j);
    jint *peak_idx_p = env->GetIntArrayElements(peak_idx_j, nullptr);
    std::vector<int> peak_idx(peak_idx_p, peak_idx_p + len);

    // Calculate heart rate
    std::vector<double> x;
    std::vector<double> y;
    try
    {
        std::tie(x, y) = calculateHeartRate(dataLen, peak_idx, Fs);
    }
    catch (const std::logic_error &e)
    {
        // 處理異常的代碼...
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught logic_error: %s", e.what());
    }
    catch (const std::runtime_error &e)
    {
        // 處理異常的代碼...
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught runtime_error: %s", e.what());
    }
    catch (std::exception &e)
    {
        // Other exceptions
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught exception: %s", e.what());
    }
    // Convert C++ vectors to Java Lists
    jobject x_j = vectorToJList(env, x);
    jobject y_j = vectorToJList(env, y);

    // Find the HRData class and its constructor
    jclass resultClass = env->FindClass("com/flowehealth/internal/models/datastructs/HRData");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(Ljava/util/List;Ljava/util/List;)V");

    // Create a new object of HRData and return
    jobject result = env->NewObject(resultClass, constructor, x_j, y_j);

    // Release the Java double array elements
    env->ReleaseIntArrayElements(peak_idx_j, peak_idx_p, JNI_ABORT);
    // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "End calculateHeartRate");
    return result;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calculatePoincarePlotFromJNI(
    JNIEnv *env,
    jobject thiz,
    jintArray peak_idx_j,
    jdouble Fs)
{
    // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Start calculatePoincarePlot");
    // Convert Java double array to C++ vector
    jsize len = env->GetArrayLength(peak_idx_j);
    jint *peak_idx_p = env->GetIntArrayElements(peak_idx_j, nullptr);
    std::vector<int> peak_idx(peak_idx_p, peak_idx_p + len);

    // Calculate PoincareData
    std::vector<double> x;
    std::vector<double> y;
    try
    {
        std::tie(x, y) = calculatePoincarePlot(peak_idx, Fs);
    }
    catch (const std::logic_error &e)
    {
        // 處理異常的代碼...
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught logic_error: %s", e.what());
    }
    catch (const std::runtime_error &e)
    {
        // 處理異常的代碼...
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught runtime_error: %s", e.what());
    }
    catch (std::exception &e)
    {
        // Other exceptions
        // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Caught exception: %s", e.what());
    }

    // Convert C++ vectors to Java arrays
    jobject x_j = vectorToJList(env, x);
    jobject y_j = vectorToJList(env, y);

    // Find the PoincareData class and its constructor
    jclass resultClass = env->FindClass("com/flowehealth/internal/models/datastructs/PoincareData");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(Ljava/util/List;Ljava/util/List;)V");

    // Create a new object of PoincareData and return
    jobject result = env->NewObject(resultClass, constructor, x_j, y_j);

    // Release the Java double array elements
    env->ReleaseIntArrayElements(peak_idx_j, peak_idx_p, JNI_ABORT);
    // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "End calculatePoincarePlot");
    return result;
}


// A simple recursive implementation of the Cooley-Tukey radix-2 Decimation in Time (DIT) FFT
std::vector<Complex> fft(const std::vector<Complex> &x)
{
    int N = x.size();

    // Base case
    if (N == 1)
        return x;

    // Recursive FFT computation
    std::vector<Complex> even(N / 2);
    std::vector<Complex> odd(N / 2);
    for (int i = 0; i < N / 2; ++i)
    {
        even[i] = x[i * 2];
        odd[i] = x[i * 2 + 1];
    }

    std::vector<Complex> fftEven = fft(even);
    std::vector<Complex> fftOdd = fft(odd);

    // Combine
    std::vector<Complex> result(N);
    for (int k = 0; k < N / 2; ++k)
    {
        Complex t = std::exp(Complex(0, -2 * PI * k / N)) * fftOdd[k];
        result[k] = fftEven[k] + t;
        result[k + N / 2] = fftEven[k] - t;
    }

    return result;
}

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calculateFFTFromJNI(JNIEnv *env, jobject thiz, jdoubleArray data, jint FFT_SIZE)
{
    // 1. Get input data length and allocate FFT setup
    jsize length = env->GetArrayLength( data);
    if (length < FFT_SIZE) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Input data length is less than FFT_SIZE.");
        return env->NewDoubleArray(FFT_SIZE); // Return empty result
    }
    PFFFT_Setup *setup = pffft_new_setup(FFT_SIZE, PFFFT_REAL);
    // 2. Prepare the input data
    jdouble *inputData = env->GetDoubleArrayElements(data, nullptr);
    auto *in = (float *)malloc(sizeof(float) * FFT_SIZE);
    // If the length exceeds FFT_SIZE, take the last FFT_SIZE data
    for (int i = 0; i < FFT_SIZE; ++i) {
        in[i] = (float)inputData[length - FFT_SIZE + i];
    }
    // 3. Execute FFT
    auto *out = (float *)malloc(sizeof(float) * FFT_SIZE);
    if (!in || !out) {
        // Handle memory allocation failure
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Memory allocation failed.");
        return env->NewDoubleArray(FFT_SIZE); // Return or handle the error as needed
    }
    pffft_transform_ordered(setup, in, out, nullptr, PFFFT_FORWARD);
    // 4. Return the results
    jdoubleArray resultArray = env->NewDoubleArray(FFT_SIZE);
    jdouble *resultData = env->GetDoubleArrayElements(resultArray, nullptr);
    for (int i = 0; i < FFT_SIZE; ++i) {
        resultData[i] = (jdouble)out[i];
    }
    env->ReleaseDoubleArrayElements(resultArray, resultData, 0);
    env->ReleaseDoubleArrayElements(data, inputData, JNI_ABORT);

    // Cleanup
    free(in);
    free(out);
    pffft_destroy_setup(setup);

    return resultArray;
//    jdouble *c_array = env->GetDoubleArrayElements(data, nullptr);
//    jsize len = env->GetArrayLength(data);
//
//    std::vector<Complex> inputData(c_array, c_array + len);
//    std::vector<Complex> fftResult = fft(inputData);
//
//    // Transform the complex FFT result to a double array for the JNI interface
//    std::vector<double> fftResultDouble(fftResult.size() * 2);
//    for (int i = 0; i < fftResult.size(); ++i)
//    {
//        fftResultDouble[i * 2] = fftResult[i].real();
//        fftResultDouble[i * 2 + 1] = fftResult[i].imag();
//    }
//
//    jdoubleArray result = env->NewDoubleArray(fftResultDouble.size());
//    env->SetDoubleArrayRegion(result, 0, fftResultDouble.size(), fftResultDouble.data());
//
//    env->ReleaseDoubleArrayElements(data, c_array, 0);
//
//    return result;
}

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_flowehealth_internal_services_BluetoothLeService_handlePacketNative(JNIEnv *env, jobject thiz, jbyteArray data)
{
    jbyte *bytes = env->GetByteArrayElements(data, nullptr);
    jsize len = env->GetArrayLength(data);

    std::vector<double> rawData;
    rawData.reserve(len / 2);

    for (jsize i = 0; i < len; i += 2)
    {
        if (i + 1 < len)
        {
            unsigned short bytesInt = ((bytes[i + 1] & 0xFF) << 8) | (bytes[i] & 0xFF);
            double voltage = static_cast<double>(bytesInt) / 1000.0;
            rawData.push_back(voltage);
        }
    }

    jdoubleArray result = env->NewDoubleArray(rawData.size());
    env->SetDoubleArrayRegion(result, 0, rawData.size(), rawData.data());

    env->ReleaseByteArrayElements(data, bytes, 0);

    return result;
}


extern "C" JNIEXPORT jobject JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calculateDoubleIntegralFromJNI(
        JNIEnv *env, jobject thiz, jdoubleArray data, jdouble Fs, jdouble DC_bias) {
    // 獲得輸入數據
    jdouble *input = env->GetDoubleArrayElements(data, nullptr);
    jsize input_size = env->GetArrayLength(data);


    std::vector<double> single_integration_result(input_size, 0.0);
    std::vector<double> double_integration_result(input_size, 0.0);
    integrate_for_epg(input, input_size, DC_bias, Fs, single_integration_result, double_integration_result);

    jobject x_j = vectorToJList(env, single_integration_result);
    jobject y_j = vectorToJList(env, double_integration_result);

    jclass resultClass = env->FindClass("com/flowehealth/internal/models/datastructs/DoubleIntegrationData");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(Ljava/util/List;Ljava/util/List;)V");


    // Create a new object of HRData and return
    jobject result = env->NewObject(resultClass, constructor, x_j, y_j);

    // Release the Java double array elements
    env->ReleaseDoubleArrayElements(data, input, 0);

    // 返回結果
    return result;
}

struct GaussianResidual {
    GaussianResidual(double x, double y)
            : x_(x), y_(y) {}

    template <typename T>
    bool operator()(const T* const amplitude1, const T* const mean1, const T* const stddev1,
                    const T* const amplitude2, const T* const mean2, const T* const stddev2,
                    const T* const amplitude3, const T* const mean3, const T* const stddev3,
                    T* residual) const {
        T y_pred = (*amplitude1 * exp(-pow((x_ - *mean1) / (*stddev1), 2) / T(2.0)) / (pow(*stddev1, 2)) * (pow((x_ - *mean1) / (*stddev1), 2) - T(1.0))) +
                   (*amplitude2 * exp(-pow((x_ - *mean2) / (*stddev2), 2) / T(2.0)) / (pow(*stddev2, 2)) * (pow((x_ - *mean2) / (*stddev2), 2) - T(1.0))) +
                   (*amplitude3 * exp(-pow((x_ - *mean3) / (*stddev3), 2) / T(2.0)) / (pow(*stddev3, 2)) * (pow((x_ - *mean3) / (*stddev3), 2) - T(1.0)));

        residual[0] = y_ - y_pred;
        return true;
    }

private:
    const double x_;
    const double y_;
};

//
//extern "C" JNIEXPORT jobject JNICALL
//Java_com_flowehealth_internal_controllers_EPGDataController_calculateGaussiansFromJNI(
//       JNIEnv* env,
//       jobject,
//       jdoubleArray rawData,
//       jdouble DC,
//       jdouble Fs) {
//   jdouble* rawDataPtr = env->GetDoubleArrayElements(rawData, nullptr);
//   jsize rawDataLength = env->GetArrayLength(rawData);
//   std::vector<double> rawDataNorm(rawDataLength);
//   double minVal = *std::min_element(rawDataPtr, rawDataPtr+rawDataLength);
//   double maxVal = *std::max_element(rawDataPtr, rawDataPtr+rawDataLength);
////    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "minVal: %f", minVal);
////    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "maxVal: %f", maxVal);
//   for(int i = 0; i < rawDataLength; ++i) {
//       rawDataNorm[i] = (rawDataPtr[i] - DC);
//   }
////    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "rawDataNorm[0]: %f", rawDataNorm[0]);
////    for (int i = 0; i < rawDataLength; ++i) {
////        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "rawDataNorm[%d]: %f", i, rawDataNorm[i]);
////    }
//
//   // Initialize Gaussian parameters
//   double amplitude1 = 1.0, mean1 = 0.2, stddev1 = 0.05;
//   double amplitude2 = 1.0, mean2 = 0.4, stddev2 = 0.08;
//   double amplitude3 = 1.0, mean3 = 0.8, stddev3 = 0.1;
//
//   ceres::Problem problem;
//
//   for (int i = 0; i < rawDataLength; ++i) {
//       double x = static_cast<double>(i) / Fs;
//       problem.AddResidualBlock(
//               new ceres::AutoDiffCostFunction<GaussianResidual, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1>(
//                       new GaussianResidual(x, rawDataNorm[i])),
//               nullptr,
//               &amplitude1, &mean1, &stddev1,
//               &amplitude2, &mean2, &stddev2,
//               &amplitude3, &mean3, &stddev3);
//   }
//
//   double peakBound = (1 / Fs) * rawDataLength;
//   problem.SetParameterLowerBound(&amplitude1, 0, 0.001);
//   problem.SetParameterLowerBound(&amplitude2, 0, 0.001);
//   problem.SetParameterLowerBound(&amplitude3, 0, 0.001);
//   problem.SetParameterLowerBound(&mean1, 0, 0.001);
//   problem.SetParameterLowerBound(&mean2, 0, 0.001);
//   problem.SetParameterLowerBound(&mean3, 0, 0.001);
////    problem.SetParameterUpperBound(&mean1, 0, peakBound);
////    problem.SetParameterUpperBound(&mean2, 0, peakBound);
////    problem.SetParameterUpperBound(&mean3, 0, peakBound);
//   problem.SetParameterLowerBound(&stddev1, 0, 0.001);
//   problem.SetParameterLowerBound(&stddev2, 0, 0.001);
//   problem.SetParameterLowerBound(&stddev3, 0, 0.001);
//
//   // Assuming you have a ceres::Solver object set up with the necessary options
//   ceres::Solver::Options options;
//   ceres::Solver::Summary summary;
//   ceres::Solve(options, &problem, &summary);
//
//   // Create the pulseWave list with the optimized parameters
//   std::vector<double> pulseWave(rawDataLength);
//   std::vector<double> LoGWave(rawDataLength);
//   std::vector<double> gaussianWave1(rawDataLength);
//   std::vector<double> gaussianWave2(rawDataLength);
//   std::vector<double> gaussianWave3(rawDataLength);
//   double diffSum = 0.0;
//   for (int i = 0; i < rawDataLength; ++i) {
//       double x = static_cast<double>(i) / Fs;
//       double x_minus_mean1 = x - mean1;
//       double x_minus_mean2 = x - mean2;
//       double x_minus_mean3 = x - mean3;
//
//       double exp_part1 = exp(-pow(x_minus_mean1 / stddev1, 2) / 2.0);
//       double exp_part2 = exp(-pow(x_minus_mean2 / stddev2, 2) / 2.0);
//       double exp_part3 = exp(-pow(x_minus_mean3 / stddev3, 2) / 2.0);
//
//       double gaussian1 = amplitude1 * exp_part1;
//       double gaussian2 = amplitude2 * exp_part2;
//       double gaussian3 = amplitude3 * exp_part3;
//
//       pulseWave[i] = gaussian1 + gaussian2 + gaussian3;
//
//       LoGWave[i] = gaussian1/pow(stddev1, 2) * (pow(x_minus_mean1 / stddev1, 2) - 1.0) +
//                    gaussian2/pow(stddev2, 2) * (pow(x_minus_mean2 / stddev2, 2) - 1.0) +
//                    gaussian3/pow(stddev3, 2) * (pow(x_minus_mean3 / stddev3, 2) - 1.0);
//
//       gaussianWave1[i] = gaussian1;
//       gaussianWave2[i] = gaussian2;
//       gaussianWave3[i] = gaussian3;
//
//       // Calculate the difference between the estimated and the original data
//       diffSum += pow(LoGWave[i] - rawDataNorm[i], 2);
//   }
//
//   // Calculate the mean squared error
//   double mse = diffSum / (rawDataLength);
//
//   // Log the mse
//   // __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "MSE: %f", mse);
//
//   // Release the rawDataPtr as we don't need it anymore
//   env->ReleaseDoubleArrayElements(rawData, rawDataPtr, JNI_ABORT);
//
//   std::vector<double> gaussian1Parameters = {amplitude1, mean1, stddev1};
//   std::vector<double> gaussian2Parameters = {amplitude2, mean2, stddev2};
//   std::vector<double> gaussian3Parameters = {amplitude3, mean3, stddev3};
//
//   jobject pulseWaveList = vectorToJList(env, pulseWave);
//   jobject logWaveList = vectorToJList(env, LoGWave);
//   jobject gaussian1List = vectorToJList(env, gaussian1Parameters);
//   jobject gaussian2List = vectorToJList(env, gaussian2Parameters);
//   jobject gaussian3List = vectorToJList(env, gaussian3Parameters);
//   jobject gaussian1WaveList = vectorToJList(env, gaussianWave1);
//   jobject gaussian2WaveList = vectorToJList(env, gaussianWave2);
//   jobject gaussian3WaveList = vectorToJList(env, gaussianWave3);
//
//
//   jclass gaussianModelClass = env->FindClass("com/flowehealth/internal/models/datastructs/GaussianModelData");
//   jmethodID constructor = env->GetMethodID(gaussianModelClass, "<init>", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V");
//
//   jobject result = env->NewObject(gaussianModelClass, constructor, pulseWaveList, logWaveList, gaussian1List, gaussian2List, gaussian3List, gaussian1WaveList, gaussian2WaveList, gaussian3WaveList);
//
//   return result;
//}

extern "C" JNIEXPORT jstring JNICALL
Java_com_flowehealth_internal_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

//extern "C"
//JNIEXPORT jdoubleArray JNICALL
//Java_com_flowehealth_internal_controllers_EPGDataController_calculateSecondDerivativeFromJNI(
//        JNIEnv *env, jobject thiz, jdoubleArray double_integral_data, jdouble Fs) {
//    // TODO: implement calculateSecondDerivativeFromJNI()
//    jsize length = env->GetArrayLength(double_integral_data);
//
//    // 獲取陣列元素
//    jdouble *data = env->GetDoubleArrayElements(double_integral_data, nullptr);
//
//    // 創建一個新的陣列來存儲結果
//    jdoubleArray result = env->NewDoubleArray(length);
//
//    jdouble first_diff = data[1] - 2 * data[0] + data[0];
//    env->SetDoubleArrayRegion(result, 0, 1, &first_diff);
//    for(jsize i = 1; i < length - 1; i++) {
//        jdouble diff = (data[i+1] - 2 * data[i] + data[i-1]) * Fs * Fs;
//        env->SetDoubleArrayRegion(result, i, 1, &diff);
//    }
//    jdouble last_diff = data[length-1] - 2 * data[length-1] + data[length-2];
//    env->SetDoubleArrayRegion(result, length-1, 1, &last_diff);
//
//    env->ReleaseDoubleArrayElements(double_integral_data, data, 0);
//
//    return result;
//}

extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calibrateDoubleIntegralFromJNI(
        JNIEnv *env, jobject thiz, jdoubleArray double_integral_data, jintArray peaks) {
    // Step 1: Convert the Java arrays to C++ vectors
    jdouble *data_ptr = env->GetDoubleArrayElements(double_integral_data, nullptr);
    jsize data_length = env->GetArrayLength(double_integral_data);
    std::vector<double> data_vector(data_ptr, data_ptr + data_length);

    jsize peaks_length = env->GetArrayLength(peaks);
    jint* peaks_ptr = env->GetIntArrayElements(peaks, nullptr);
    std::vector<int> peaks_vector(peaks_ptr, peaks_ptr + peaks_length);

    calibrate_for_epg_integration(data_vector, peaks_vector);

    env->ReleaseDoubleArrayElements(double_integral_data, data_ptr, 0);
    env->ReleaseIntArrayElements(peaks, peaks_ptr, 0);

    // Convert std::vector<double> to jdoubleArray and return
    jdoubleArray jresult = env->NewDoubleArray(data_vector.size());
    env->SetDoubleArrayRegion(jresult, 0, data_vector.size(), data_vector.data());
    return jresult;
}

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_flowehealth_internal_controllers_EPGDataController_calculateDifferentiationFromJNI(
        JNIEnv* env,
        jobject /* this */,
        jdoubleArray inputArray,
        jdouble samplingRate,
        jdouble DC_bias) {

    // Convert jdoubleArray to std::vector
    jsize length = env->GetArrayLength(inputArray);
    jdouble* input_ptr = env->GetDoubleArrayElements(inputArray, nullptr);
    std::vector<double> input_data(input_ptr, input_ptr + length);
    env->ReleaseDoubleArrayElements(inputArray, input_ptr, JNI_ABORT);

    std::vector<double> outputData;

    differentiate_for_epg(input_data, samplingRate, outputData, DC_bias);

    // Convert std::vector to jdoubleArray
    jdoubleArray result = env->NewDoubleArray(length);
    env->SetDoubleArrayRegion(result, 0, length, &outputData[0]);
    return result;
}
