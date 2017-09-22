package co.otaoto.ui.show

import co.otaoto.api.Api
import co.otaoto.api.ShowError
import co.otaoto.api.ShowSuccess
import co.otaoto.ui.base.BaseViewModel

class ShowViewModel : BaseViewModel<ShowViewModel.View>() {
    interface View : BaseViewModel.View {
        fun renderGate()
        fun renderShow()
        fun renderGone()
        fun showSecret(secret: String)
        fun moveToCreateScreen()
    }

    private enum class State(val path: String, val render: View.() -> Unit) {
        GATE("gate", View::renderGate),
        SHOW("show", View::renderShow),
        GONE("gone", View::renderGone);
    }

    private lateinit var state: State
    private var slug: String? = null
    private var key: String? = null
    private var secret: String? = null

    lateinit var api: Api

    internal fun init(view: View, pathSegments: List<String>, api: Api) {
        this.api = api
        if (pathSegments.size == 3) {
            slug = pathSegments[1]
            key = pathSegments[2]
            val pathState: State = State.values().find { it.path == pathSegments[0] } ?: State.GONE
            state = if (pathState == State.SHOW) State.GATE else pathState // Never show on launch
        } else {
            state = State.GONE
        }
        view.renderState()
    }

    internal fun clickCreateAnother(view: View) {
        view.moveToCreateScreen()
    }

    internal suspend fun clickReveal(view: View) {
        val slug = this.slug
        val key = this.key
        if (state != State.GATE || slug == null || key == null) return
        val result = api.show(slug, key)
        return when (result) {
            is ShowSuccess -> {
                val secret = result.plainText
                this.secret = secret
                view.showSecret(secret)
                state = State.SHOW
                view.renderState()
            }
            is ShowError -> {
                state = State.GONE
                view.renderState()
            }
        }
    }

    private fun View.renderState() = (state.render)()
}
