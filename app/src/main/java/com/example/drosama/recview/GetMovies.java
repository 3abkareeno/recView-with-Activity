package com.example.drosama.recview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetMovies extends AsyncTask<String, Void, Movie[]> {
    private ProgressDialog progressDialog;
    private Context context;
    private final String LOG_TAG = GetMovies.class.getSimpleName();

    public interface SetOnSuccess{
        void onSuccess(Movie[] result);
    }

    private static SetOnSuccess aftermath;

    public GetMovies(SetOnSuccess aftermath, Context context) {
        this.aftermath = aftermath;
        this.context = context;
    }

    private Movie[] getMovieDataFromJson(String jsonStr) throws JSONException {

        final String OWM_RESULTS = "results";
        final String OWM_POSTERPATH = "poster_path";
        final String OWM_ADULT = "adult";
        final String OWM_OVERVIEW = "overview";
        final String OWM_TITLE = "title";
        final String OWM_RELEASE = "release_date";
        final String OWM_ID = "id";
        final String OWM_USERVOTE = "vote_average";

        JSONObject json = new JSONObject(jsonStr);
        JSONArray movieArray = json.getJSONArray(OWM_RESULTS);

        Movie[] resultMovs = new Movie[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            String poster;
            Boolean adult;
            String overview;
            String title;
            String release;
            String id;
            String userVote;

            JSONObject movie = movieArray.getJSONObject(i);

            poster = movie.getString(OWM_POSTERPATH);
            adult = movie.getBoolean(OWM_ADULT);
            overview = movie.getString(OWM_OVERVIEW);
            title = movie.getString(OWM_TITLE);
            release = movie.getString(OWM_RELEASE);
            id = movie.getString(OWM_ID);
            userVote = movie.getString(OWM_USERVOTE);

            resultMovs[i] = new Movie(poster, title, adult, overview, release, id, userVote);
        }
        for (Movie m : resultMovs) {
            Log.v(LOG_TAG, "Movie entry: " + m.getTitle());
        }
        return resultMovs;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        try {
            URL url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            movieJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        aftermath.onSuccess(result);
        progressDialog.dismiss();
    }
}
