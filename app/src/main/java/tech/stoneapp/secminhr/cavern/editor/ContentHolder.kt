package tech.stoneapp.secminhr.cavern.editor

import androidx.databinding.ObservableField

interface ContentHolder {
    val title: ObservableField<String>
    val content: ObservableField<String>
}