package com.chethan.demoproject.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.chethan.demoproject.R
import com.chethan.demoproject.model.Location
import kotlinx.android.synthetic.main.locations_list_fragment.*


/**
 *Created by Bhagavan Byreddy on 19/08/21.
 */
class LocationsListFragment : Fragment(),LocationsListAdapter.ItemClickListener{

    lateinit var locationsListAdapter: LocationsListAdapter
    var locationsList = mutableListOf<Location>()
    lateinit var navigationController:NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.locations_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationController = Navigation.findNavController(view)
        locationsListAdapter = LocationsListAdapter(locationsList)

        locationsRv.layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )

        locationsRv.adapter = locationsListAdapter
        locationsListAdapter.setItemClickListener(this)

        addLocationBtn.setOnClickListener {
            navigationController.navigate(R.id.action_locationsListFragment_to_mapsActivity)
        }

        val result  = getNavigationResult("resultKey")
        if(result != null && result.value != null){
            locationsList.add(result.value!!)
            updateList()
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<Location>("resultKey")

        if(locationsList.size > 0){
            locationsRv.visibility = View.VISIBLE
            noDataTv.visibility = View.GONE
        }else{
            locationsRv.visibility = View.GONE
            noDataTv.visibility = View.VISIBLE
        }
    }

    private fun updateList() {
        locationsListAdapter.notifyDataSetChanged()
        if(locationsList.size > 0){
            locationsRv.visibility = View.VISIBLE
            noDataTv.visibility = View.GONE
        }else{
            locationsRv.visibility = View.GONE
            noDataTv.visibility = View.VISIBLE
        }
    }

    fun Fragment.getNavigationResult(key: String = "resultKey") =
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Location>(key)

    override fun onItemClick(view: View, position: Int) {
        val location = locationsList.get(position)
        val bundle = Bundle()
        bundle.putString("lat",location.lat.toString())
        bundle.putString("long",location.long.toString())
        navigationController.navigate(R.id.action_locationsListFragment_to_weatherFragment,bundle)
    }

    override fun onRemoveItemClick(view: View, position: Int) {
        locationsList.removeAt(position)
        updateList()
    }


}
