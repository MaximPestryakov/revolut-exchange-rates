package com.github.maximpestryakov.revolut.business

import io.reactivex.Observable

interface ObserveInternetConnectionChanges : () -> Observable<Boolean>
