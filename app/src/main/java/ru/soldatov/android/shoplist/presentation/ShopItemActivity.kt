package ru.soldatov.android.shoplist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import ru.soldatov.android.shoplist.R
import ru.soldatov.android.shoplist.domain.ShopItem

class ShopItemActivity : AppCompatActivity() {

    private lateinit var inputName: TextInputLayout
    private lateinit var inputCount: TextInputLayout
    private lateinit var editTextName: EditText
    private lateinit var editTextCount: EditText
    private lateinit var buttonSave: Button

    private var screenMode = UNKNOWN_MODE
    private var shopItemId = ShopItem.UNDEFINED_ID

    private val viewModel: ShopItemViewModel by lazy {
        ViewModelProvider(this)[ShopItemViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        parseIntent()
        initViews()
        when (screenMode) {
            EXTRA_MODE_ADD -> launchScreenAdd()
            EXTRA_MODE_EDIT -> launchScreenEdit()
        }
    }

    private fun initViews() {
        inputName = findViewById(R.id.til_name)
        inputCount = findViewById(R.id.til_count)
        editTextName = findViewById(R.id.et_name)
        editTextCount = findViewById(R.id.et_count)
        buttonSave = findViewById(R.id.save_button)
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

    private fun launchScreenAdd() {
        addTextWatchListener()
        buttonSave.setOnClickListener {
            viewModel.addShopItem(editTextName.text.toString(), editTextCount.text.toString())
            closeScreen()
        }
    }

    private fun launchScreenEdit() {
        addTextWatchListener()
        viewModel.getShopItem(shopItemId)
        viewModel.shopItem.observe(this) {
            val name = it.name
            val count = it.count
            editTextName.setText(name)
            editTextCount.setText(count.toString())
        }
        buttonSave.setOnClickListener {
            viewModel.editShopItem(editTextName.text.toString(), editTextCount.text.toString())
            closeScreen()
        }
    }

    private fun addTextWatchListener() {
        viewModel.errorInputName.observe(this) {
            if (it) {
                inputName.error = getString(R.string.error_input_name)
            } else {
                inputName.error = null
            }
        }
        viewModel.errorInputCount.observe(this) {
            if (it) {
                inputCount.error = getString(R.string.error_input_name)
            } else {
                inputCount.error = null
            }
        }
        editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //empty
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) {
                //empty
            }
        })
        editTextCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //empty
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) {
                //empty
            }
        })
    }

    private fun closeScreen() {
        viewModel.canClosedScreen.observe(this) {
            finish()
        }
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