package com.example.paging

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paging.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MyViewModel by viewModels()
    lateinit var myAdapter: RecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        myAdapter = RecyclerViewAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = myAdapter
        }
        viewModel.responseData.observe(this) {
            if(it!=null) {
                binding.progress.visibility=View.GONE
                myAdapter.submitData(lifecycle, it)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
//                    query?.let {
//                        if (it.isEmpty()) {
//                            viewModel.searchData("%%").observe(this@MainActivity) { pagingData ->
//
//                                myAdapter.submitData(lifecycle, pagingData)
//                            }
//                        }
//                        viewModel.searchData("%$it%").observe(this@MainActivity) { pagingData ->
//                            binding.progress.visibility=View.GONE
//                            myAdapter.submitData(lifecycle, pagingData)
//                        }
//                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {

                            viewModel.searchData("%$it%").observe(this@MainActivity) { pagingData ->
                                if (pagingData!=null){
                                    myAdapter.submitData(lifecycle, pagingData)
                                }
                                else
                                {
                                    binding.progress.visibility=View.VISIBLE
                                    viewModel.searchDataFromApi(query = it).observe(this@MainActivity){pagingData->
                                        binding.progress.visibility=View.GONE
                                        myAdapter.submitData(lifecycle, pagingData)
                                    }
                                }

                            }
                    }
                    return true
                }
            })

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}