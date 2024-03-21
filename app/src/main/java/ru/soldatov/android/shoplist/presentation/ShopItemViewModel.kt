package ru.soldatov.android.shoplist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.soldatov.android.shoplist.data.ShopListRepositoryImpl
import ru.soldatov.android.shoplist.domain.AddShopItemUseCase
import ru.soldatov.android.shoplist.domain.EditShopItemUseCase
import ru.soldatov.android.shoplist.domain.GetShopItemUseCase
import ru.soldatov.android.shoplist.domain.ShopItem

class ShopItemViewModel : ViewModel() {

    private val shopListRepository = ShopListRepositoryImpl

    private val addShopItemUseCase = AddShopItemUseCase(shopListRepository)
    private val getShopItemUseCase = GetShopItemUseCase(shopListRepository)
    private val editShopItemUseCase = EditShopItemUseCase(shopListRepository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    private val _canClosedScreen = MutableLiveData<Unit>()
    val canClosedScreen: LiveData<Unit>
        get() = _canClosedScreen

    fun getShopItem(shopItemId: Int) {
        val item = getShopItemUseCase.getShopItem(shopItemId)
        _shopItem.value = item
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val resultValid = validField(name, count)
        if (resultValid) {
            val shopItem = ShopItem(name, count, true)
            addShopItemUseCase.addShopItem(shopItem)
            finishWork()
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val resultValid = validField(name, count)
        if (resultValid) {
            _shopItem.value?.let {
                val shopItem = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(shopItem)
                finishWork()
            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validField(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun finishWork() {
        _canClosedScreen.value = Unit
    }
}