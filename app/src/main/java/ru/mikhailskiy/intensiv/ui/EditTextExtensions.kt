package ru.mikhailskiy.intensiv.ui

import android.text.Editable
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import io.reactivex.Observable

fun EditText.afterTextChanged(action: (s: Editable?) -> Unit) =
    addTextChangedListener(afterTextChanged = action)

fun EditText.onTextChanged(action: (p0: CharSequence?, p1: Int, p2: Int, p3: Int) -> Unit) =
    addTextChangedListener(onTextChanged = action)

fun EditText.onTextChangedObservable(): Observable<String> {
    return Observable.create<String> { emitter ->
        onTextChanged { p0, p1, p2, p3 ->
            emitter.onNext(p0.toString())
        }
    }
}
