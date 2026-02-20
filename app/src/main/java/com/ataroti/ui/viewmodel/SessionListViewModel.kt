package com.ataroti.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ataroti.data.repo.AtarotiRepository

class SessionListViewModel(repo: AtarotiRepository) : ViewModel() {
    val sessions = repo.sessions()
}

class SessionListViewModelFactory(private val repo: AtarotiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SessionListViewModel(repo) as T
}
