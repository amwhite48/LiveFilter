package com.example.livefilter.models;

import android.util.Log;

import com.example.livefilter.Details;

import java.util.HashMap;
import java.util.Map;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSwirlFilter;

public class AppliedFilter {

    public static final String TAG = "AppliedFilter Object";
    public static final String[] EFFECTS = {"brightness", "contrast", "hue", "grayscale", "sepia", "saturation", "swirl", "gaussian blur", "sharpen", "posterize", "highlights", "shadows", "exposure"};
    public static final int[] DEFAULT_INTENSITIES = {50, 25, 25, 100, 100, 50, 50, 100, 50, 4, 100, 0, 50};

    HashMap<String, Integer> appliedEffects;
    HashMap<String, GPUImageFilter> appliedFilters;

    GPUImageFilterGroup filtersApplied;

    // constructor that initializes filter group
    public AppliedFilter() {
        appliedEffects = new HashMap<>();
        appliedFilters = new HashMap<>();
        filtersApplied = new GPUImageFilterGroup();

    }

    // alternate constructor that generates a filter from the values passed in
    public AppliedFilter(String[] effectNames, int[] effectIntensities) {
        appliedEffects = new HashMap<>();
        appliedFilters = new HashMap<>();
        filtersApplied = new GPUImageFilterGroup();

        // add each filter with given intensity to camera
        for(int i = 0; i < effectNames.length; i++) {
            this.addFilter(effectNames[i]);
            this.adjustFilter(effectNames[i], effectIntensities[i]);
        }
    }


    // check if given filter is applied
    public boolean filterApplied(String filterName) {
        return appliedEffects.containsKey(filterName);
    }

    // return list of all possible effects
    public String[] getEffectsList() {
        return EFFECTS;
    }

    // return list of default intensities
    public int[] getDefaultIntensities() {
        return DEFAULT_INTENSITIES;
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
            case "swirl":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageSwirlFilter swirlFilter = (GPUImageSwirlFilter) appliedFilters.get(filterName);
                // swirl radius ranges from 0.0f to 1.0f
                float radius = (float) value / 100.0f;
                swirlFilter.setRadius(radius);
                break;
            case "gaussian blur":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageGaussianBlurFilter gaussianBlurFilter = (GPUImageGaussianBlurFilter) appliedFilters.get(filterName);
                // gaussian blur radius ranges from 0.0f to 1.0f
                float blurSize = (float) value / 100.0f;
                gaussianBlurFilter.setBlurSize(blurSize);
                break;
            case "sharpen":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageSharpenFilter sharpenFilter = (GPUImageSharpenFilter) appliedFilters.get(filterName);
                // sharpen intensities ranges from -4.0f to 4.0f
                float sharpness = (float) value / 12.5f - 4.0f;
                sharpenFilter.setSharpness(sharpness);
                break;
            case "posterize":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImagePosterizeFilter posterizeFilter = (GPUImagePosterizeFilter) appliedFilters.get(filterName);
                // posterize levels ranges from 1 to 256
                float floatLevels = (float) value * 2.55f + 1f;
                int levels = (int)floatLevels;
                posterizeFilter.setColorLevels(levels);
                break;
            case "highlights":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageHighlightShadowFilter highlightFilter = (GPUImageHighlightShadowFilter) appliedFilters.get(filterName);
                // highlight ranges from 0.0f to 1.0f
                float highlight = (float) value / 100.0f;
                highlightFilter.setHighlights(highlight);
                break;
            case "shadows":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageHighlightShadowFilter shadowFilter = (GPUImageHighlightShadowFilter) appliedFilters.get(filterName);
                // shadow intensities ranges from 0.0f to 1.0f
                float shadow = (float) value / 100.0f;
                shadowFilter.setShadows(shadow);
                break;
            case "exposure":
                appliedEffects.remove(filterName);
                appliedEffects.put(filterName, value);
                GPUImageExposureFilter exposureFilter = (GPUImageExposureFilter) appliedFilters.get(filterName);
                // exposure intensities ranges from -10.0f to 10.0f
                float exposure = (float) value / 5.0f - 10.0f;
                exposureFilter.setExposure(exposure);
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
            case "swirl":
                GPUImageFilter swirlFilter = new GPUImageSwirlFilter();
                appliedFilters.put(filterName, swirlFilter);
                appliedEffects.put(filterName, 50);
                filtersApplied.addFilter(swirlFilter);
                break;
            case "gaussian blur":
                GPUImageFilter gaussianBlurFilter = new GPUImageGaussianBlurFilter();
                appliedFilters.put(filterName, gaussianBlurFilter);
                appliedEffects.put(filterName, 100);
                filtersApplied.addFilter(gaussianBlurFilter);
                break;
            case "sharpen":
                GPUImageFilter sharpenFilter = new GPUImageSharpenFilter();
                appliedFilters.put(filterName, sharpenFilter);
                appliedEffects.put(filterName, 50);
                filtersApplied.addFilter(sharpenFilter);
                break;
            case "posterize":
                GPUImageFilter posterizeFilter = new GPUImagePosterizeFilter();
                appliedFilters.put(filterName, posterizeFilter);
                appliedEffects.put(filterName, 4);
                filtersApplied.addFilter(posterizeFilter);
                break;
            case "highlights":
                GPUImageFilter highlightFilter = new GPUImageHighlightShadowFilter();
                appliedFilters.put(filterName, highlightFilter);
                appliedEffects.put(filterName, 100);
                filtersApplied.addFilter(highlightFilter);
                // highlight and shadows use same filter type
                appliedFilters.put("shadows", highlightFilter);
                appliedEffects.put("shadows", 0);
                break;
            case "shadows":
                GPUImageFilter shadowFilter = new GPUImageHighlightShadowFilter();
                appliedFilters.put(filterName, shadowFilter);
                appliedEffects.put(filterName, 0);
                filtersApplied.addFilter(shadowFilter);
                // highlight and shadows use same filter type
                appliedFilters.put("highlights", shadowFilter);
                appliedEffects.put("highlights", 100);
                break;
            case "exposure":
                GPUImageFilter exposureFilter = new GPUImageExposureFilter();
                appliedFilters.put(filterName, exposureFilter);
                appliedEffects.put(filterName, 50);
                filtersApplied.addFilter(exposureFilter);
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

    // get a list of applied filters for representing filter as a parse object
    public String[] getAppliedNames() {
        String[] appliedNames = new String[appliedEffects.size()];
        int counter = 0;
        for(Map.Entry effect: appliedEffects.entrySet()) {
            appliedNames[counter] = (String) effect.getKey();
            counter++;
        }
        return appliedNames;
    }

    // given a list of effects applied, get the intensities in the same order
    public int[] getAppliedIntensities(String[] filterNames) {
        // ToDo: better error checking
        int[] filterIntensities = new int[filterNames.length];

        for(int i = 0; i < filterNames.length; i++) {
            if(appliedEffects.containsKey(filterNames[i])) {
                filterIntensities[i] = appliedEffects.get(filterNames[i]);
            }
        }

        return filterIntensities;
    }

}
