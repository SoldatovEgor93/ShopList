package ru.soldatov.android.shoplist.presentation

import androidx.lifecycle.ViewModel
import ru.soldatov.android.shoplist.data.ShopListRepositoryImpl
import ru.soldatov.android.shoplist.domain.DeleteShopItemUseCase
import ru.soldatov.android.shoplist.domain.EditShopItemUseCase
import ru.soldatov.android.shoplist.domain.GetShopListUseCase
import ru.soldatov.android.shoplist.domain.ShopItem

class MainViewModel : ViewModel() {

    private val shopListRepository = ShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(shopListRepository)
    private val editShopItemUseCase = EditShopItemUseCase(shopListRepository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(shopListRepository)

    val shopList = getShopListUseCase.getShopList()

    fun editShopItem(shopItem: ShopItem) {
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(shopItem)
    }

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }
}