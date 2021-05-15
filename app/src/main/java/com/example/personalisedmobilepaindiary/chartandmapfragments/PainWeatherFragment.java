package com.example.personalisedmobilepaindiary.chartandmapfragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalisedmobilepaindiary.R;
import com.example.personalisedmobilepaindiary.databinding.PainWeatherFragmentBinding;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

public class PainWeatherFragment extends Fragment
{
    private PainWeatherFragmentBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = PainWeatherFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.startDateInput.setShowSoftInputOnFocus(false);
        binding.endDateInput.setShowSoftInputOnFocus(false);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
        // Configure date input box
        binding.startDateInput.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus)
            {
                DatePickerDialog dialog = new DatePickerDialog(requireActivity(), 0, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        binding.startDateInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)); // set default date
                dialog.show();
            }
        });
        binding.startDateInput.setOnClickListener(v ->
        {

            DatePickerDialog dialog = new DatePickerDialog(requireActivity(), 0, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                {
                    binding.startDateInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)); // set default date
            dialog.show();

        });
        binding.endDateInput.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus)
            {
                DatePickerDialog dialog = new DatePickerDialog(requireActivity(), 0, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        binding.endDateInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year); // set default date
                    }
                }, 2021, 4, 23);
                dialog.show();
            }
        });
        binding.endDateInput.setOnClickListener(v ->
        {

            DatePickerDialog dialog = new DatePickerDialog(requireActivity(), 0, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                {
                    binding.endDateInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)); // set default date
            dialog.show();

        });
        // Initialize weather variable spinner
        String[] weatherVars = new String[]{"temperature", "humidity", "pressure"};
        ArrayAdapter<String> weatherArrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, weatherVars);
        weatherArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.weatherVarSpinner.setAdapter(weatherArrayAdapter);
        // When clear, refresh the current page.
        binding.clearBtm.setOnClickListener(v ->
        {
            getParentFragmentManager().beginTransaction().replace(R.id.chart_container_view, new PainWeatherFragment()).commit();
        });
        DatabaseViewModel datebaseViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(DatabaseViewModel.class);
        // When load button clicked, start showing the graph.
        binding.loadGraphBtm.setOnClickListener(v ->
        {
            // Get the records of current user.
            CompletableFuture<List<PainRecord>> painRecordList = datebaseViewModel.getAllBylistAndUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            painRecordList.thenApply(painRecords ->
            {
                getActivity().runOnUiThread(() ->
                {
                    // Records number must be at least 2.
                    if (painRecords.size() > 1)
                    {
                        // If date input boxes are polluted.
                        if (!(binding.startDateInput.getText().toString().equals("") || binding.endDateInput.getText().toString().equals("")))
                        {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date startDate = null;
                            Date endDate = null;
                            try
                            {
                                // Read date from inputs.
                                startDate = dateFormat.parse(binding.startDateInput.getText().toString());
                                endDate = dateFormat.parse(binding.endDateInput.getText().toString());
                            } catch (ParseException e)
                            {
                            }
                            try
                            {
                                // Dates input must be in the time period of all the records.
                                if (startDate.getTime() < dateFormat.parse(painRecords.get(0).date).getTime() ||
                                        endDate.getTime() > dateFormat.parse(painRecords.get(painRecords.size() - 1).date).getTime())
                                {
                                    binding.textView2.setText("You must enter dates within the time period of all the records");
                                }
                                // Start date must be ealier than end date.
                                else if (startDate.getTime() > endDate.getTime())
                                {
                                    binding.textView2.setText("Start date must be earlier than end date");
                                } else
                                {
                                    // Retrieve data from pain records according to the date inputs.
                                    List<String> dateList = new ArrayList<>();
                                    List<Entry> entriesPainLevel = new ArrayList<Entry>();
                                    List<PainRecord> records = new ArrayList<>();
                                    for (PainRecord painRecord : painRecords)
                                    {
                                        if (startDate.getTime() <= dateFormat.parse(painRecord.date).getTime() &&
                                                endDate.getTime() >= dateFormat.parse(painRecord.date).getTime())
                                        {
                                            records.add(painRecord);
                                        }
                                    }
                                    // // Records number must be at least 2.
                                    if (records.size() < 2)
                                    {
                                        binding.textView2.setText("You have less than 2 records! No graph available!");
                                        return;
                                    }
                                    for (PainRecord painRecord : records)
                                    {
                                        dateList.add(painRecord.date);
                                        entriesPainLevel.add(new Entry(records.indexOf(painRecord), painRecord.painIntensityLevel));
                                    }
                                    // Formatter to format x axis label to date format.
                                    ValueFormatter dateFormatter = new ValueFormatter()
                                    {
                                        @Override
                                        public String getAxisLabel(float value, AxisBase axis)
                                        {
                                            return dateList.get((int) value);
                                        }
                                    };
                                    // Formatter to format x axis label to int format.
                                    ValueFormatter intFormatter = new ValueFormatter()
                                    {
                                        @Override
                                        public String getAxisLabel(float value, AxisBase axis)
                                        {
                                            return ((int) value) + "";
                                        }
                                    };
                                    // According to the user selection of the spinner, retrieve weather variable data for the second y axis.
                                    List<Entry> entriesWeatherVal = new ArrayList<Entry>();
                                    if (binding.weatherVarSpinner.getSelectedItem().toString().equals("temperature"))
                                    {
                                        for (PainRecord painRecord : records)
                                        {
                                            entriesWeatherVal.add(new Entry(records.indexOf(painRecord), (float) painRecord.weather.temperature));
                                        }
                                    } else if (binding.weatherVarSpinner.getSelectedItem().toString().equals("humidity"))
                                    {
                                        for (PainRecord painRecord : records)
                                        {
                                            entriesWeatherVal.add(new Entry(records.indexOf(painRecord), (float) painRecord.weather.humidity));
                                        }
                                    } else
                                    {
                                        for (PainRecord painRecord : records)
                                        {
                                            entriesWeatherVal.add(new Entry(records.indexOf(painRecord), (float) painRecord.weather.pressure));
                                        }
                                    }
                                    // Initialize dataset.
                                    LineDataSet dataSetPainLevel = new LineDataSet(entriesPainLevel, "Pain Level");
                                    LineDataSet dataSetWeatherVal = new LineDataSet(entriesWeatherVal, "Weather Value");
                                    dataSetPainLevel.setAxisDependency(YAxis.AxisDependency.LEFT);
                                    dataSetWeatherVal.setAxisDependency(YAxis.AxisDependency.RIGHT);
                                    dataSetPainLevel.setValueTextColor(ColorTemplate.rgb("#000000"));
                                    dataSetPainLevel.setDrawCircleHole(false);
                                    dataSetPainLevel.setValueTextSize(10f);
                                    dataSetPainLevel.setLineWidth(2f);
                                    dataSetPainLevel.setCircleRadius(3f);
                                    dataSetPainLevel.setColor(Color.RED);
                                    dataSetWeatherVal.setValueTextColor(ColorTemplate.rgb("#000000"));
                                    dataSetWeatherVal.setDrawCircleHole(false);
                                    dataSetWeatherVal.setValueTextSize(10f);
                                    dataSetWeatherVal.setLineWidth(2f);
                                    dataSetWeatherVal.setCircleRadius(3f);
                                    Description description = binding.lineChart.getDescription();
                                    description.setText("");

                                    LineData lineData = new LineData(dataSetPainLevel, dataSetWeatherVal);
                                    // Initialize line chart
                                    binding.lineChart.setData(lineData);
                                    binding.lineChart.getXAxis().setValueFormatter(dateFormatter);
                                    binding.lineChart.getXAxis().setLabelRotationAngle(-60);
                                    binding.lineChart.getAxisLeft().setValueFormatter(intFormatter);
                                    binding.lineChart.getAxisLeft().setAxisMaximum(10);
                                    binding.lineChart.getAxisLeft().setAxisMinimum(0);
                                    binding.lineChart.getAxisLeft().setLabelCount(11, true);
                                    binding.lineChart.getAxisRight().setAxisMinimum(0);

                                    // Configure x axis label count.
                                    int XLabelCount = dateList.size();
                                    if (XLabelCount > 8)
                                    {
                                        XLabelCount = XLabelCount / (XLabelCount / 10);
                                    }
                                    binding.lineChart.getXAxis().setLabelCount(XLabelCount, true);
                                    binding.lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                    binding.lineChart.setExtraBottomOffset(40f);
                                    binding.lineChart.getLegend().setDrawInside(true);
                                    binding.lineChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
                                    binding.lineChart.getLegend().setXOffset(220);
                                    binding.lineChart.getLegend().setYOffset(20);
                                    binding.lineChart.getLegend().setTextSize(13f);
                                    binding.lineChart.cancelDragAndDrop();
                                    binding.lineChart.invalidate();
                                    binding.textView2.setText("Successful!!");
                                    binding.clearBtm.setEnabled(true);
                                    binding.loadGraphBtm.setEnabled(false);

                                    // Create value array to perform correlation test.
                                    double[] painLevels = new double[entriesPainLevel.size()];
                                    double[] weatherVars1 = new double[entriesWeatherVal.size()];
                                    double[][] dataSet = new double[entriesPainLevel.size()][2];
                                    for (Entry entry : entriesPainLevel)
                                    {
                                        painLevels[entriesPainLevel.indexOf(entry)] = entry.getY();
                                    }

                                    for (Entry entry : entriesWeatherVal)
                                    {
                                        weatherVars1[entriesWeatherVal.indexOf(entry)] = entry.getY();
                                    }
                                    for (int i = 0; i < entriesPainLevel.size(); i++)
                                    {
                                        dataSet[i] = new double[]{weatherVars1[i], painLevels[i]};
                                    }
                                    // Perform correlation test when button clicked.
                                    RealMatrix m = MatrixUtils.createRealMatrix(dataSet);
                                    binding.correlationTestBtm.setOnClickListener(v1 ->
                                    {
                                        PearsonsCorrelation pc = new PearsonsCorrelation(m);
                                        RealMatrix corM = pc.getCorrelationMatrix();
                                        // significant test of the correlation coefficient (p-value)
                                        RealMatrix pM = pc.getCorrelationPValues();
                                        binding.correlationTestResultR.setText("correlation: " + corM.getEntry(0, 1));
                                        binding.correlationTestResultP.setText("p value: " + pM.getEntry(0, 1));
                                    });
                                }
                            } catch (ParseException e)
                            {
                                Log.e("a", " ads");
                            }

                        } else
                        {
                            binding.textView2.setText("You must enter both of start date and end date!!");
                        }
                    } else
                    {
                        binding.textView2.setText("You have less than 2 records! No graph available!");
                    }

                });

                return null;
            });

        });

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
