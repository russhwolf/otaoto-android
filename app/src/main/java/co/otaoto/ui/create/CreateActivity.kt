package co.otaoto.ui.create

import android.content.Context
import android.content.Intent
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.TextView
import android.widget.Toast
import butterknife.OnClick
import co.otaoto.R
import co.otaoto.ui.base.BaseActivity
import co.otaoto.ui.confirm.ConfirmActivity
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class CreateActivity : BaseActivity<CreateContract.ViewModel, CreateViewModel.Factory>(), CreateContract.View {
    companion object {
        fun newIntent(context: Context) = Intent(context, CreateActivity::class.java)
    }

    private val inputLayout: TextInputLayout inline get() = create_input_layout
    private val inputTextView: TextView inline get() = create_input_edittext

    override val layoutRes: Int get() = R.layout.activity_create

    override fun onResume() {
        super.onResume()
        viewModel.run {
            moveToConfirmTrigger.observeNonNull { moveToConfirmScreen(it.secret, it.slug, it.key) }
            errorTrigger.observeNonNull { showError(it) }
            passwordVisibleHackTrigger.observeNonNull { performPasswordVisibleHack() }
        }
    }

    private fun moveToConfirmScreen(secret: String, slug: String, key: String) {
        startActivity(ConfirmActivity.newIntent(this, secret, slug, key))
        finish()
    }

    private fun showError(exception: Throwable) {
        Toast.makeText(this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
    }

    private fun performPasswordVisibleHack() {
        // h4x! We want to start in visible password mode and there doesn't seem to be a default to do this.
        inputLayout.post { inputLayout.findViewById<View>(R.id.text_input_password_toggle).callOnClick() }
        viewModel.reportPasswordVisibleHackComplete()
    }

    @OnClick(R.id.create_submit_button)
    protected fun onSubmitClick() {
        launch(UI) {
            val text = inputTextView.text ?: ""
            viewModel.submit(text.toString())
        }
    }
}
