package ru.soldatov.android.shoplist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.soldatov.android.shoplist.R

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedFragment {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var fabAddShopItem: FloatingActionButton
    private var shopItemContainer: FragmentContainerView? = null

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shopItemContainer = findViewById(R.id.shop_item_container)
        if (savedInstanceState != null) {
            if (shopItemContainer == null) {
                supportFragmentManager.popBackStack()
            }
        }
        fabAddShopItem = findViewById(R.id.button_add_shop_item)
        setupRecyclerView()
        viewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
        }
        fabAddShopItem.setOnClickListener {
            if (shopItemContainer == null) {
                val intent = ShopItemActivity.newIntentModeAdd(this)
                startActivity(intent)
            } else {
                val fragment = ShopItemFragment.newInstanceModeAdd()
                launchFragment(fragment)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_shop_list)
        shopListAdapter = ShopListAdapter()
        with(recyclerView) {
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_ENABLED,
                ShopListAdapter.MAX_POOL
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_DISABLED,
                ShopListAdapter.MAX_POOL
            )
        }
        setupLongClickListener()
        setupClickListener()
        setupSwipeClickListener()
    }

    private fun setupSwipeClickListener() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val shopItem = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(shopItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = {
            if (shopItemContainer == null) {
                val intent = ShopItemActivity.newIntentModeEdit(this, it.id)
                startActivity(intent)
            } else {
                val fragment = ShopItemFragment.newInstanceModeEdit(it.id)
                launchFragment(fragment)
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.editShopItem(it)
        }
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun finishedFragment() {
        supportFragmentManager.popBackStack()
    }
}