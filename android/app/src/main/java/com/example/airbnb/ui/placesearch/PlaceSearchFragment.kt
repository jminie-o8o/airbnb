package com.example.airbnb.ui.placesearch

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airbnb.R
import com.example.airbnb.databinding.FragmentPlaceSearchBinding
import com.example.airbnb.ui.common.RangeValidator
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.getInstance


class PlaceSearchFragment : Fragment() {
    private lateinit var binding: FragmentPlaceSearchBinding
    private val adapter = PlaceSearchAdapter()
    private val viewModel: PlaceSearchViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_place_search, container, false)

        listenBackButton()
        setRecyclerView()
        updateAdapter()
        listenSearchWordChange()
        updateSearchWord()
        updateBySearchingWordExist()
        listenClearButtonClicked()

        binding.btnTemp.setOnClickListener {
            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setCalendarConstraints(limitRange().build())
                .setTitleText("여행 일정")
                .build()
            dateRangePicker.show(childFragmentManager, "date_picker")
            dateRangePicker.addOnPositiveButtonClickListener {
                val startDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(it.first)
                val endDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(it.second)
                Log.d("test", "startDate: $startDate, endDate : $endDate")
                findNavController().navigate(PlaceSearchFragmentDirections.actionPlaceSearchFragmentToPriceSettingFragment())
            }

        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun limitRange(): CalendarConstraints.Builder {

        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarStart: Calendar = GregorianCalendar.getInstance()
        val calendarEnd: Calendar = GregorianCalendar.getInstance()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE
        val formatted = current.format(formatter)
        val currentDate = formatted.split("-")

        calendarStart.set(
            currentDate[0].toInt(),
            currentDate[1].toInt() - 1,
            currentDate[2].toInt() - 1
        )
        calendarEnd.set(2999, 12, 31)

        val minDate = calendarStart.timeInMillis
        val maxDate = calendarEnd.timeInMillis

        constraintsBuilderRange.setStart(minDate)
        constraintsBuilderRange.setEnd(maxDate)

        constraintsBuilderRange.setValidator(RangeValidator(minDate, maxDate))

        return constraintsBuilderRange
    }

    private fun listenClearButtonClicked() {
        binding.btnInitSearchWord.setOnClickListener {
            binding.etSearchWord.text.clear()
        }
    }

    private fun listenSearchWordChange() {
        binding.etSearchWord.addTextChangedListener {
            viewModel.handleSearchWord(it.toString())
        }
    }

    private fun setRecyclerView() {
        binding.rvSearchPlace.adapter = adapter
        binding.rvSearchPlace.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.getPlaceList("")
    }


    private fun updateSearchWord() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchWord.collect {
                    viewModel.getPlaceList(it)
                }
            }
        }
    }

    private fun updateAdapter() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.placeList.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun updateBySearchingWordExist() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.popularPlaceExplain.collect {
                    binding.btnInitSearchWord.visibility = it?.let {
                        View.INVISIBLE
                    } ?: View.VISIBLE

                    binding.tvPopularPlaceExplain.visibility =
                        it?.let {
                            binding.tvPopularPlaceExplain.text = it
                            View.VISIBLE
                        } ?: View.GONE
                }
            }
        }
    }

    private fun listenBackButton() {
        binding.btnPlaceSearchBack.setOnClickListener {
            findNavController().navigate(PlaceSearchFragmentDirections.actionPlaceSearchFragmentToSearchFragment())
        }
    }

}
