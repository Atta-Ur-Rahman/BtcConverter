package com.techease.btcConverter;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Double.valueOf;

public class MainActivity_conversion extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;

    String url = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=USD,EUR,CHF";
    TextView tvBtcUsd, tvBtcMxn, tvBtcMxntoUsd, tvBtcChf;
    TextView tvEthUsd, tvEthMxn, tvEthMxntoUsd, tvEthChf;
    TextView tvEthToday, tvBtcToday;
    ImageView alertOne, alertTwo, ivBitcoinList;
    TextView dateOne, dateTwo;
    String Date, strSplitDates;

    static Double compareValue;

    Double BTCPriceInUSD, BTCPriceInMXN, ETHPriceInUSD, ETHPriceInMXN, USDtoMXNRate;

    static NotificationManager notificationManager;

    boolean isAlert = true;
    static final Handler handler = new Handler();
    Intent serviceIntent;

    String strListUrl;

    List<PreviousDataModel> previousDataModels = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;


    private Dialog customPreviousDialog;


    PriceAdapter priceAdapter;

    boolean aBooleanCliclPreviousPrice = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversion_bitcoin);

        // logout = (TextView) findViewById(R.id.logout);


        java.util.Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String strCurrentDate = df.format(c);

        ///reduce date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date result = cal.getTime();


        strSplitDates = String.valueOf(getDaysBetweenDates(result, c));


        Log.d("datess", String.valueOf(getDaysBetweenDates(result, c)));

        String strReduceDate = df.format(result);


        sharedPreferences = getSharedPreferences("Reg", 0);
        editor = sharedPreferences.edit();

        String compare = sharedPreferences.getString("setCompare", "10.00");

        if (compare != null) {
            Log.d("compare", compare);
            compareValue = Double.valueOf(compare);
        } else if (compare == null) {
            compareValue = 17.00;
        }


        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        //tabOne
        TabHost.TabSpec specOne = tabHost.newTabSpec("Bitcoin");
        specOne.setContent(R.id.tabBitcoin);
        specOne.setIndicator("Bitcoin");
        specOne.setIndicator(new Tab(getApplicationContext(), R.drawable.bitcoin_b, "Bitcoin"));
        tabHost.addTab(specOne);


        //tabTwo
        TabHost.TabSpec specTwo = tabHost.newTabSpec("Ethereum");
        specTwo.setContent(R.id.tabEthereum);
        specTwo.setIndicator("Ethereum");
        specTwo.setIndicator(new Tab(getApplicationContext(), R.drawable.ethereum_b, "Ethereum"));
        tabHost.addTab(specTwo);


        ivBitcoinList = findViewById(R.id.iv_bitcoin_list);
        tvBtcUsd = findViewById(R.id.tvBtc_usd);
        tvBtcMxn = findViewById(R.id.tvBtc_mxn);
        tvBtcMxntoUsd = findViewById(R.id.tvBtc_MxnUsd);
        tvBtcChf = findViewById(R.id.tvBtc_chf);
        tvEthUsd = findViewById(R.id.tvEth_Usd);
        tvEthMxn = findViewById(R.id.tvEth_mxn);
        tvEthMxntoUsd = findViewById(R.id.tvEth_MxnUsd);
        tvEthChf = findViewById(R.id.tvEth_chf);
        tvBtcToday = findViewById(R.id.tvBtcToday);
        tvEthToday = findViewById(R.id.tvEthToday);
        alertOne = findViewById(R.id.imgAlert); //bitcoin
        alertTwo = findViewById(R.id.imgAlertTwo); //Ethereum
        dateOne = findViewById(R.id.tvDate1);
        dateTwo = findViewById(R.id.tvDate2);


        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Date = sdf.format(date);

        dateOne.setText(Date);
        dateTwo.setText(Date);

        alertOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showValueBox();
            }
        });

        alertTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showValueBox();
            }
        });


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1 * 60 * 1000); // every 1 minutes
                coinRates();
            }
        }, 0);


        strListUrl = "http://api.coindesk.com/v1/bpi/historical/close.json?start=" + strReduceDate + "&end=" + strCurrentDate;


        ivBitcoinList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aBooleanCliclPreviousPrice) {
                    previousDataModels.clear();
                    listCoins();
                    aBooleanCliclPreviousPrice = false;
                }


            }
        });


    }

    private void customDailog() {
        customPreviousDialog = new Dialog(this);
        customPreviousDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customPreviousDialog.setCancelable(true);
        customPreviousDialog.setContentView(R.layout.custom_previous_layout);

        RecyclerView rvPrevious = customPreviousDialog.findViewById(R.id.rv_previous);
        rvPrevious.hasFixedSize();
        rvPrevious.setLayoutManager(new LinearLayoutManager(this));


        priceAdapter = new PriceAdapter(this, previousDataModels);
        rvPrevious.setAdapter(priceAdapter);

        aBooleanCliclPreviousPrice = true;


        customPreviousDialog.show();
        customPreviousDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        customPreviousDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }


    public void coinRates() {

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(MainActivity_conversion.this, ""+response, Toast.LENGTH_SHORT).show();
                        Log.d("Response", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject objectBTC = jsonObject.getJSONObject("BTC");
                            JSONObject objectETH = jsonObject.getJSONObject("ETH");

                            tvBtcToday.setText(objectBTC.getString("USD"));
                            tvBtcUsd.setText(objectBTC.getString("USD"));
                            tvBtcMxn.setText(objectBTC.getString("EUR"));
                            tvBtcChf.setText(objectBTC.getString("CHF"));


                            tvEthToday.setText(objectETH.getString("USD"));
                            tvEthUsd.setText(objectETH.getString("USD"));
                            tvEthMxn.setText(objectETH.getString("EUR"));
                            tvEthChf.setText(objectETH.getString("CHF"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity_conversion.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("error", String.valueOf(error));


            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }


    public void listCoins() {


        final List<String> items = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, strListUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ResponseList", response);


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject objectBTC = jsonObject.getJSONObject("bpi");


                            String strApiValue = String.valueOf(objectBTC);
                            String strRemoveLeftBreketAPI = strApiValue.replace("{", "");
                            String strRemoveRightBreketAPI = strRemoveLeftBreketAPI.replace("}", "");
                            String[] strSplitValueAPI = strRemoveRightBreketAPI.split(",");


                            for (int i = 0; i < strSplitValueAPI.length; i++) {


                                String strUSDValue = strSplitValueAPI[i];
                                String strUsdReplace = strUSDValue.replace("\"", "");
                                String string = strUsdReplace.replace(":", "   :  ");

                                PreviousDataModel previousDataModel = new PreviousDataModel();
                                previousDataModel.setPreviousDatePrice(string);
                                previousDataModels.add(previousDataModel);

                            }
                            customDailog();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity_conversion.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("error", String.valueOf(error));


            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }


    public void showValueBox() {


        final View mView = getLayoutInflater().inflate(R.layout.value_box, null);
        final EditText value = mView.findViewById(R.id.etValue);
        Button save = mView.findViewById(R.id.btnSave);

        value.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        value.setText(compareValue.toString());
        final AlertDialog valueBox = new AlertDialog.Builder(MainActivity_conversion.this).create();
        valueBox.setView(mView);
        valueBox.show();
        valueBox.setCancelable(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                compareValue = parseDouble(value.getText().toString());
                editor.putString("setCompare", String.valueOf(compareValue));

                editor.commit();
                valueBox.dismiss();
                isAlert = true;


            }
        });


    }


    public static List<String> getDaysBetweenDates(Date startdate, Date enddate) {
        List<String> dates = new ArrayList<String>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate)) {
            Date result = calendar.getTime();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            dates.add(df.format(result));
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }
}


