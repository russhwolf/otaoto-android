package co.otaoto.ui.confirm

import android.arch.lifecycle.MutableLiveData
import co.otaoto.ui.base.BaseViewModel
import co.otaoto.ui.confirm.ConfirmViewModel.View
import javax.inject.Inject
import javax.inject.Named

class ConfirmViewModel(private val secret: String, slug: String, key: String) : BaseViewModel<View>() {
    companion object {
        internal const val PARAM_SECRET = "secret"
        internal const val PARAM_SLUG = "slug"
        internal const val PARAM_KEY = "key"
    }

    interface View : BaseViewModel.View {
        fun showSecret()
        fun hideSecret()
        fun setSecretText(text: String)
        fun setLinkUrl(url: String)
        fun shareUrl(url: String)
        fun moveToCreateScreen()
    }

    class Factory @Inject constructor() : BaseViewModel.Factory<ConfirmViewModel>() {
        @Inject
        @field:Named(PARAM_SECRET)
        protected lateinit var secret: String

        @Inject
        @field:Named(PARAM_SLUG)
        protected lateinit var slug: String

        @Inject
        @field:Named(PARAM_KEY)
        protected lateinit var key: String

        override fun create(): ConfirmViewModel = ConfirmViewModel(secret, slug, key)
    }

    private val url: String = "https://otaoto.co/gate/$slug/$key"

    private val secretVisible = MutableLiveData<Boolean>()
    private val shareTrigger = MutableLiveData<Unit>()
    private val moveToCreateTrigger = MutableLiveData<Unit>()

    override fun init(view: View) {
        super.init(view)
        view.setLinkUrl(url)
        view.observe(secretVisible) { visible: Boolean? ->
            if (visible == true) {
                setSecretText(secret)
                showSecret()
            } else {
                hideSecret()
                setSecretText("")
            }
        }
        view.observe(shareTrigger) {
            shareUrl(url)
        }
        view.observe(moveToCreateTrigger) {
            moveToCreateScreen()
        }
    }

    internal fun setSecretVisible(visible: Boolean) {
        secretVisible.value = visible
    }

    internal fun clickLink() {
        shareTrigger.value = Unit
    }

    internal fun clickCreateAnother() {
        moveToCreateTrigger.value = Unit
    }
}
