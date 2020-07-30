package com.example.livefilter.models;

import android.util.Log;

import java.util.HashMap;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;

public class AppliedFilter {

    public static final String TAG = "AppliedFilter Object";
    public static final String[] EFFECTS = {"brightness", "contrast", "hue", "grayscale", "sepia", "saturation"};

    HashMap<String, Integer> appliedEffects;
    HashMap<String, GPUImageFilter> appliedFilters;

    GPUImageFilterGroup filtersApplied;

    // constructor that initializes filter group
    public AppliedFilter() {
        appliedEffects = new HashMap<>();
        appliedFilters = new HashMap<>();
        filtersApplied = new GPUImageFilterGroup();

    }


    // check if given filter is applied
    public boolean filterApplied(String filterName) {
        return appliedEffects.containsKey(filterName);
    }

    // return list of all possible effects
    public String[] getEffectsList() {
        return EFFECTS;
    }

    // get filters applied and add to camera
    public GPUImageFilterGroup getFiltersApplied() {
        return filtersApplied;
    }

    // adjust value of a filter on a filter already in the group
    // seekbar has int values from 0 to 100
    public void adjustFilter(String filterName, int value) {

        //ToDo: errorcheck value that gets passed in

        switch(filterName) {
            case "brightness":
                // replace value of applied effect
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageBrightnessFilter brightnessFilter = (GPUImageBrightnessFilter) appliedFilters.get(filterName);
                // brightness ranges from -1.0f to 1.0f
                float brightness = (float)value / 50.0f - 1.0f;
                brightnessFilter.setBrightness(brightness);
                break;
            case "contrast":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageContrastFilter contrastFilter = (GPUImageContrastFilter) appliedFilters.get(filterName);
                // contrast ranges from 0.0f to 4.0f
                float contrast = (float)value / 25.0f;
                contrastFilter.setContrast(contrast);
                break;
            case "hue":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageHueFilter hueFilter = (GPUImageHueFilter) appliedFilters.get(filterName);
                // hue ranges from 0.0f to 360.0f
                float hue = (float) value * 3.6f;
                hueFilter.setHue(hue);
                break;
            case "saturation":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageSaturationFilter saturationFilter = (GPUImageSaturationFilter) appliedFilters.get(filterName);
                // saturation ranges from 0.0f to 2.0f
                float saturation = (float) value / 50.0f;
                saturationFilter.setSaturation(saturation);
                break;
            default:
                Log.i(TAG, "invalid filter adjustment");
                break;

        }

    }

    // add filter to filter group if not already added
    public void addFilter(String filterName) {
        // switch statement with all different filters
        switch (filterName) {
            case "brightness":
                GPUImageFilter brightnessFilter = new GPUImageBrightnessFilter();
                appliedFilters.put(filterName, brightnessFilter);
                appliedEffects.put(filterName, 50);
                filtersApplied.addFilter(brightnessFilter);
                break;
            case "contrast":
                GPUImageFilter contrastFilter = new GPUImageContrastFilter();
                appliedFilters.put(filterName, contrastFilter);
                appliedEffects.put(filterName, 25);
                filtersApplied.addFilter(contrastFilter);
                break;
            case "hue":
                GPUImageFilter hueFilter = new GPUImageHueFilter();
                appliedFilters.put(filterName, hueFilter);
                appliedEffects.put(filterName, 25);
                filtersApplied.addFilter(hueFilter);
                break;
            case "grayscale":
                GPUImageFilter grayscaleFilter = new GPUImageGrayscaleFilter();
                appliedFilters.put(filterName, grayscaleFilter);
                appliedEffects.put(filterName, 100);
                filtersApplied.addFilter(grayscaleFilter);
                break;
            case "sepia":
                GPUImageFilter sepiaFilter = new GPUImageSepiaToneFilter();
                appliedFilters.put(filterName, sepiaFilter);
                appliedEffects.put(filterName, 100);
                filtersApplied.addFilter(sepiaFilter);
                break;
            case "saturation":
                GPUImageFilter saturationFilter = new GPUImageSaturationFilter();
                appliedFilters.put(filterName, saturationFilter);
                appliedEffects.put(filterName, 50);
                filtersApplied.addFilter(saturationFilter);
                break;
            default:
                Log.i(TAG, "invalid filter name");
                break;
        }

    }

    // check numerical value of String that should be present on seekbar
    public int getFilterValue(String filterName) {
        // ToDo: error check String
        return appliedEffects.get(filterName);
    }

}
