package ru.soldatov.android.shoplist.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import ru.soldatov.android.shoplist.R
import ru.soldatov.android.shoplist.domain.ShopItem

class ShopItemFragment : Fragment() {

    private lateinit var onEditingFinishedFragment: OnEditingFinishedFragment

    private lateinit var inputName: TextInputLayout
    private lateinit var inputCount: TextInputLayout
    private lateinit var editTextName: EditText
    private lateinit var editTextCount: EditText
    private lateinit var buttonSave: Button

    private var screenMode: String = UNKNOWN_MODE
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    private val viewModel: ShopItemViewModel by lazy {
        ViewModelProvider(this)[ShopItemViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedFragment) {
            onEditingFinishedFragment = context
        } else {
            throw RuntimeException("Activity must be implement OnEditingFinishedFragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseParams()
        initViews(view)
        when (screenMode) {
            MODE_ADD -> launchScreenAdd()
            MODE_EDIT -> launchScreenEdit()
        }
    }

    private fun initViews(view: View) {
        inputName = view.findViewById(R.id.til_name)
        inputCount = view.findViewById(R.id.til_count)
        editTextName = view.findViewById(R.id.et_name)
        editTextCount = view.findViewById(R.id.et_count)
        buttonSave = view.findViewById(R.id.save_button)
    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Screen mode is not found")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Not found id")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID)
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
        viewModel.shopItem.observe(viewLifecycleOwner) {
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
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            if (it) {
                inputName.error = getString(R.string.error_input_name)
            } else {
                inputName.error = null
            }
        }
        viewModel.errorInputCount.observe(viewLifecycleOwner) {
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
        viewModel.canClosedScreen.observe(viewLifecycleOwner) {
            onEditingFinishedFragment.finishedFragment()
        }
    }

    interface OnEditingFinishedFragment {

        fun finishedFragment()
    }

    companion object {

        private const val SCREEN_MODE = "extra_screen_mode"
        private const val MODE_ADD = "extra_mode_add"
        private const val MODE_EDIT = "extra_mode_edit"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"

        private const val UNKNOWN_MODE = ""

        fun newInstanceModeAdd(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceModeEdit(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}