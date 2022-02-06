package com.example.android.newsapp;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final int SUCCESS_CODE = 200;

    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String WEB_TITLE = "webTitle";
    private static final String SECTION_NAME = "sectionName";
    private static final String WEB_URL = "webUrl";
    private static final String TAGS = "tags";
    private static final String NO_AVAILABLE = "N/A";
    private static final int TAGS_INDEX = 0;

    private QueryUtils() {
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<Story> fetchStoryData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractFeatureFromJson(jsonResponse);
    }


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == SUCCESS_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static List<Story> extractFeatureFromJson(String storyJSON) {
        if (TextUtils.isEmpty(storyJSON)) {
            return null;
        }

        List<Story> stories = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(storyJSON);

            JSONObject response = baseJsonResponse.getJSONObject(RESPONSE);

            JSONArray storyArray = response.getJSONArray(RESULTS);

            for (int i = 0; i < storyArray.length(); i++) {

                JSONObject currentStory = storyArray.getJSONObject(i);

                String date = currentStory.optString(WEB_PUBLICATION_DATE);

                String title = currentStory.optString(WEB_TITLE);

                String section = currentStory.optString(SECTION_NAME);

                String url = currentStory.optString(WEB_URL);

                String author = NO_AVAILABLE;

                if (currentStory.has(TAGS)) {

                    JSONArray tagsArray = currentStory.getJSONArray(TAGS);

                    if (!tagsArray.isNull(TAGS_INDEX)) {
                        JSONObject currentStoryTags = tagsArray.getJSONObject(TAGS_INDEX);

                        if (currentStory.has(WEB_TITLE)) {
                            author = currentStoryTags.optString(WEB_TITLE);
                        }
                    }
                }

                Story story = new Story(date, title, section, url, author);

                stories.add(story);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the story JSON results", e);
        }
        return stories;
    }
}
