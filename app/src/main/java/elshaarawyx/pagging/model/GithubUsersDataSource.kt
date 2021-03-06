package elshaarawyx.pagging.model

import android.arch.paging.PageKeyedDataSource
import elshaarawyx.pagging.retrofit.GithubAPIsFactory
import elshaarawyx.pagging.retrofit.UsersAPIs
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import retrofit2.Response

/**
 * Created by elshaarawy on 10/14/18.
 */
class GithubUsersDataSource : PageKeyedDataSource<Long, UserEntity>() {

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, UserEntity>) {
        loadData(0) {
            callback.onResult(it, null, it.lastOrNull()?.id)
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, UserEntity>) {
        params.key?.let {
            loadData(it) { result ->
                callback.onResult(result, result.lastOrNull()?.id)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, UserEntity>) {
        params.key?.let {
            loadData(it) { result ->
                callback.onResult(result, result.firstOrNull()?.id)
            }
        }
    }

    private inline fun loadData(since: Long, crossinline onSuccess: (List<UserEntity>) -> Unit) {
        GlobalScope.launch {
            GithubAPIsFactory()
                    .create(UsersAPIs::class.java)
                    .retrieveUsers(since)
                    .await()
                    .takeIf { it.isSuccessful }
                    ?.let(Response<List<UserEntity>>::body)
                    ?.let(onSuccess)
        }
    }
}