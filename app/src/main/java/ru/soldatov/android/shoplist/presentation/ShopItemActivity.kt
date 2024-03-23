package ru.soldatov.android.shoplist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.soldatov.android.shoplist.R
import ru.soldatov.android.shoplist.domain.ShopItem

class ShopItemActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedFragment {

    private var screenMode = UNKNOWN_MODE
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        parseIntent()
        if (savedInstanceState == null) {
            launchRightMode()
        }
    }

    private fun launchRightMode() {
        val fragment = when(screenMode) {
            EXTRA_MODE_ADD -> ShopItemFragment.newInstanceModeAdd()
            EXTRA_MODE_EDIT -> ShopItemFragment.newInstanceModeEdit(shopItemId)
            else -> throw RuntimeException("Unknown mode $screenMode")
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Screen mode is not find")
        }
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != EXTRA_MODE_ADD && mode != EXTRA_MODE_EDIT) {
            throw RuntimeException("unknown mode $mode")
        }
        screenMode = mode
        if (screenMode == EXTRA_MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Id shop item is not find")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    override fun finishedFragment() {
        finish()
    }

    companion object {

        private const val EXTRA_SCREEN_MODE = "extra_screen_mode"
        private const val EXTRA_MODE_ADD = "extra_mode_add"
        private const val EXTRA_MODE_EDIT = "extra_mode_edit"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"

        private const val UNKNOWN_MODE = ""

        fun newIntentModeAdd(context: Context): Intent {
            return Intent(context, ShopItemActivity::class.java).apply {
                putExtra(EXTRA_SCREEN_MODE, EXTRA_MODE_ADD)
            }
        }

        fun newIntentModeEdit(context: Context, shopItemId: Int): Intent {
            return Intent(context, ShopItemActivity::class.java).apply {
                putExtra(EXTRA_SCREEN_MODE, EXTRA_MODE_EDIT)
                putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            }
        }
    }
}