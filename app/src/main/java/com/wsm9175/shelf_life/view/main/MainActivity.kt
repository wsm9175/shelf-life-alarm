package com.wsm9175.shelf_life.view.main

import SwipeController
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wsm9175.shelf_life.databinding.ActivityMainBinding
import com.wsm9175.shelf_life.db.entity.FoodEntity
import com.wsm9175.shelf_life.receiver.Receiver
import com.wsm9175.shelf_life.view.adapter.FoodListRVAdapter
import com.wsm9175.shelf_life.view.add.ItemAddActivity
import com.wsm9175.shelf_life.view.detail.DetailActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var foodEntityList: List<FoodEntity>
    private val ENTITY = "entity"

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
            when {
                deniedPermissionList.isNotEmpty() -> {
                    val map = deniedPermissionList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                    }
                    map[DENIED]?.let {
                        // 단순히 권한이 거부 되었을 때
                    }
                    map[EXPLAINED]?.let {
                        // 권한 요청이 완전히 막혔을 때(주로 앱 상세 창 열기)
                    }
                }
                else -> {
                    // 모든 권한이 허가 되었을 때
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.activity = this@MainActivity
        setContentView(binding.root)

        viewModel.foodItemList.observe(this, Observer {
            Log.d(TAG, "data size : " + it.size)
            foodEntityList = it
            if (it.size == 0) {
                binding.text.visibility = View.VISIBLE
            } else {
                binding.text.visibility = View.INVISIBLE
            }
            settingFoodList()
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult.launch(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            )
        }

        registerAlarm()
        setUpRecyclerView()
    }

    private fun settingFoodList() {
        Log.d(TAG, "settingFoodList")
        val foodListRVAdapter = FoodListRVAdapter(this, foodEntityList)
        binding.rv.adapter = foodListRVAdapter
        binding.rv.layoutManager = LinearLayoutManager(this)

        foodListRVAdapter.itemClick = object : FoodListRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val selectItem = foodEntityList[position]
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(ENTITY, selectItem.id)
                startActivity(intent)
            }
        }
    }

    fun moveToAddActivity() {
        startActivity(Intent(this, ItemAddActivity::class.java))
    }

    private fun registerAlarm() {
        val receiver = ComponentName(applicationContext, Receiver::class.java)

        this.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Receiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
        }

        Log.d(TAG, calendar.time.toString())

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_DAY,
            pendingIntent
        )
    }

    private fun setUpRecyclerView() {
        val swipeController = SwipeController(object : SwipeControllerActions {
            override fun onLeftClicked(pos: Int) {
                TODO("Not yet implemented")
            }

            override fun onRightClicked(pos: Int) {
                viewModel.deleteEntity(foodEntityList[pos])
            }

            override fun onReset(pos: Int) {
                TODO("Not yet implemented")
            }

        }, resources)
        val itemHelper = ItemTouchHelper(swipeController)
        itemHelper.attachToRecyclerView(binding.rv)
        binding.rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })
    }
}

interface SwipeControllerActions {
    fun onLeftClicked(pos: Int)
    fun onRightClicked(pos: Int)
    fun onReset(pos: Int)
}