<div>
<img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=Android&logoColor=white" />
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white" />
<img src="https://img.shields.io/badge/writer-kym1924-yellow?&style=flat&logo=Android"/>
</div>

# Paging 3
Pokemon list using *Paging 3 Library* and [PokeAPI](https://pokeapi.co/).
And referenced [this site](https://pokemon.fandom.com/ko/wiki/%EB%B6%84%EB%A5%98:%ED%83%80%EC%9E%85_%EC%83%89%EA%B9%94_%ED%8B%80).
<br><br>
<img width=360 height=760 src="images/image.gif"/>

#### 1. Initialize
```groovy
dependencies {
  def paging_version = "3.1.1"

  implementation "androidx.paging:paging-runtime:$paging_version"

  // alternatively - without Android dependencies for tests
  testImplementation "androidx.paging:paging-common:$paging_version"

  // optional - RxJava2 support
  implementation "androidx.paging:paging-rxjava2:$paging_version"

  // optional - RxJava3 support
  implementation "androidx.paging:paging-rxjava3:$paging_version"

  // optional - Guava ListenableFuture support
  implementation "androidx.paging:paging-guava:$paging_version"

  // optional - Jetpack Compose integration
  implementation "androidx.paging:paging-compose:1.0.0-alpha14"
}
```
<br>

#### 2. Benefits of Paging library
- Caching paged data in memory allows app to use system resources *efficiently* while working with paging data.
- By default, duplicate requests are avoided, allowing *efficient* use of network bandwidth and system resources.
- The adapter automatically requests data when the scroll reaches the end of the loaded data.
- Support *refresh*, *retry* and *error handling*.
- First-class support for *Kotlin coroutines and Flow*, as well as LiveData and RxJava.
<br>

#### 3. Paging Library Components
<figure>
    <img src="https://developer.android.com/topic/libraries/architecture/images/paging3-library-architecture.svg"/>
    <figcaption>Reference : https://developer.android.com/topic/libraries/architecture/paging/v3-overview</figcaption>
</figure>
<br><br>

- `Repository layer`
  - PagingSource
  - RemoteMediator
- `ViewModel layer`
  - Pager
- `UI layer`
  - PagingDataAdapter
<br>

#### 3-1. PagingSource
- Defines *how data is loaded*. That is, used to load the data page for the *PagingData* instance.
- Abstract class for handling paging for data page load expensive operations.
  - Can load data from network or local databases.
- Uses Kotlin coroutines for *async loading*.
<br>

#### 3-1-1. Define PagingSource
- PagingSource has two type parameters.
  - *Key* is used as an identifier when loading. For example, the page to send on request.
  - *Value* is the type of data loaded.
```kotlin
public abstract class PagingSource<Key : Any, Value : Any> {
    ...
}
```

- PagingSource has two methods that must be implemented.
  - *load()* indicates how to retrieve the paged data from the corresponding data source.
  - *getRefreshKey()* obtains the key used for subsequent refresh calls to PagingSource.load().
```kotlin
public abstract class PagingSource<Key : Any, Value : Any> {
    ...
    public abstract suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value>
	
    public abstract fun getRefreshKey(state: PagingState<Key, Value>): Key?
}
```
<br>

#### 3-1-2. load()
Implement this method to trigger async load.
- param : LoadParams&lt;Key>
- return : LoadResult<Key, Value>
  - LoadResult is a sealed class with *Page* and *Error*.
```kotlin
override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> {
    ...
    return try {
    ...
        LoadResult.Page(
        ...
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}
```
<br>

#### 3-1-3. LoadParams
- *loadSize* is requested number of items to load.
- From PagingConfig.enablePlaceholders, true if placeholders are enabled and the load request for this LoadParams should populate LoadResult.Page.itemsBefore and LoadResult.Page.itemsAfter if possible.
```kotlin
public sealed class LoadParams<Key : Any> constructor(
    public val loadSize: Int,
    public val placeholdersEnabled: Boolean,
) {
    ...
}
```
<br>

#### 3-1-4. LoadResult.Page
- If the paging library succeeds in loading the data, `LoadResult.Page`.
- `val data: List<Value>` is loaded data.
- `val prevKey: Key?`  is a key for previous page, null if no more can be loaded.
- `val nextKey: Key?`  is a key for next page, null if no more can be loaded.
```kotlin
public data class Page<Key : Any, Value : Any> constructor(
    val data: List<Value>,
    val prevKey: Key?,
    val nextKey: Key?,
    @IntRange(from = COUNT_UNDEFINED.toLong())
    val itemsBefore: Int = COUNT_UNDEFINED,
    @IntRange(from = COUNT_UNDEFINED.toLong())
    val itemsAfter: Int = COUNT_UNDEFINED
) : LoadResult<Key, Value>() {
    public constructor(
        data: List<Value>,
        prevKey: Key?,
        nextKey: Key?
    ) : this(data, prevKey, nextKey, COUNT_UNDEFINED, COUNT_UNDEFINED)

    init {
        require(itemsBefore == COUNT_UNDEFINED || itemsBefore >= 0) {
            "itemsBefore cannot be negative"
        }

        require(itemsAfter == COUNT_UNDEFINED || itemsAfter >= 0) {
            "itemsAfter cannot be negative"
        }
    }
    
    public companion object {
        public const val COUNT_UNDEFINED: Int = Int.MIN_VALUE

        @Suppress("MemberVisibilityCanBePrivate") // Prevent synthetic accessor generation.
        internal val EMPTY = Page(emptyList(), null, null, 0, 0)

        @Suppress("UNCHECKED_CAST") // Can safely ignore, since the list is empty.
        internal fun <Key : Any, Value : Any> empty() = EMPTY as Page<Key, Value>
    }
}
```
<br>

#### 3-1-5. Diagram load()
Diagram showing how `load()` uses and updates the key.
<figure>
    <img height = 500 src="https://developer.android.com/topic/libraries/architecture/images/paging3-source-load.svg"/>
    <figcaption>Reference : https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data</figcaption>
</figure>
<br>

#### 3-1-6. LoadResult.Error
- If the paging library encounters an error loading data,  `LoadResult.Error`.
```kotlin
public data class Error<Key : Any, Value : Any>(
    val throwable: Throwable
) : LoadResult<Key, Value>()
```
<br>

#### 3-1-7. getRefreshKey()
- Provide a Key used for the initial load for the next PagingSource due to invalidation of this PagingSource.
  - When refreshing, the key is fetched based on the current position.
- The Key is provided to load via [LoadParams.key](#3-1-2-load).
```kotlin
override fun getRefreshKey(state: PagingState<Int, PokemonEntity>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey?.plus(Int)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(Int)
    }
}
```
<br>

#### 3-1-8. closestPageToPosition()
- `closestPageToPosition()` is method to find the page key of the closest page with anchorPosition.
  - *anchorPosition* is most recently accessed index.
  - *anchorPosition* can be null if no access in the PagingData has been made yet.
- The closest page is returned by calling `closestPageToPosition()` of PagingState with anchorPosition.
  - The returned page is [LoadResult.Page](#3-1-4-loadresultpage).
  - *null* if all loaded pages are empty.
```kotlin
public fun closestPageToPosition(anchorPosition: Int): Page<Key, Value>? {
    if (pages.all { it.data.isEmpty() }) return null
	
	anchorPositionToPagedIndices(anchorPosition) { pageIndex, index ->
        return when {
            index < 0 -> pages.first()
            else -> pages[pageIndex]
        }
    }
}
```
- if prevKey is null, the returned page is first page.
- if nextKey is null, the returned page is last page.
- Pass the key returned by getRefreshKey() to the next [load()](#3-1-2-load) function call.
<br>

#### 3-1-9. Other PagingSource
- [RxPagingSource](https://developer.android.com/reference/kotlin/androidx/paging/rxjava2/RxPagingSource) for RxJava.
- [ListenableFuturePagingSource](https://developer.android.com/reference/kotlin/androidx/paging/ListenableFuturePagingSource) for ListenableFuture from Guava.
<br>

#### 3-2. Pager
- *Primary entry point into Paging*
- *PagingConfig* and *PagingSource* are required when creating a `Pager` instance.
- Provides a public API for constructing instances of *PagingData* that are exposed in reactive streams.
- Pager object provides a [LoadParams](#3-1-3-loadparams) object by calling the *load() method of the PagingSource* and receives the returned [LoadResult](#3-1-4-loadresultpage) object.
```kotlin
public class Pager<Key : Any, Value : Any>
@ExperimentalPagingApi constructor(
    config: PagingConfig,
    initialKey: Key? = null,
    remoteMediator: RemoteMediator<Key, Value>?,
    pagingSourceFactory: () -> PagingSource<Key, Value>
) {
    @JvmOverloads
    @OptIn(ExperimentalPagingApi::class)
    public constructor(
        config: PagingConfig,
        initialKey: Key? = null,
        pagingSourceFactory: () -> PagingSource<Key, Value>
    ) : this(config, initialKey, null, pagingSourceFactory)

    @OptIn(androidx.paging.ExperimentalPagingApi::class)
    public val flow: Flow<PagingData<Value>> = PageFetcher(
        pagingSourceFactory = if (
            pagingSourceFactory is SuspendingPagingSourceFactory<Key, Value>
        ) {
            pagingSourceFactory::create
        } else {
            {
                pagingSourceFactory()
            }
        },
        initialKey = initialKey,
        config = config,
        remoteMediator = remoteMediator
    ).flow
}
```
<br>

#### 3-2-1. PagingConfig
`PagingConfig` sets options regarding how content is loaded from the *PagingSource*.
```kotlin
public class PagingConfig @JvmOverloads public constructor(
    @JvmField
    public val pageSize: Int,

    @JvmField
    @IntRange(from = 0)
    public val prefetchDistance: Int = pageSize,

    @JvmField
    public val enablePlaceholders: Boolean = true,

    @JvmField
    @IntRange(from = 1)
    public val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER,
    
    @JvmField
    @IntRange(from = 2)
    public val maxSize: Int = MAX_SIZE_UNBOUNDED,

    @JvmField
    public val jumpThreshold: Int = COUNT_UNDEFINED
) {
    init {
        if (!enablePlaceholders && prefetchDistance == 0) {
            throw IllegalArgumentException(
                "Placeholders and prefetch are the only ways" +
                    " to trigger loading of more data in PagingData, so either placeholders" +
                    " must be enabled, or prefetch distance must be > 0."
            )
        }
        if (maxSize != MAX_SIZE_UNBOUNDED && maxSize < pageSize + prefetchDistance * 2) {
            throw IllegalArgumentException(
                "Maximum size must be at least pageSize + 2*prefetchDist" +
                    ", pageSize=$pageSize, prefetchDist=$prefetchDistance" +
                    ", maxSize=$maxSize"
            )
        }

        require(jumpThreshold == COUNT_UNDEFINED || jumpThreshold > 0) {
            "jumpThreshold must be positive to enable jumps or COUNT_UNDEFINED to disable jumping."
        }
    }

    public companion object {
        @Suppress("MinMaxConstant")
        public const val MAX_SIZE_UNBOUNDED: Int = Int.MAX_VALUE
        internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
    }
}
```
- `pageSize` : The number of items loaded at once from the PagingSource.
  - The only parameter need to define.
  - Small page size provides *high memory usage efficiency*, *low latency*, and easy to *avoid GC churn*.
  - Small page size is bad because the content on the page doesn't cover the entire screen.
  - Large page size is *load efficient*. However, *long latency*.
- prefetchDistance : Distance that must be accessed to call the next *load()*.
  - The default value is set to *pageSize*.
  - When the already loaded content scrolls by the set value, additional load() is called.
  - 0 indicates that no list items will be loaded until they are specifically requested. *Not recommended*.
- `enablePlaceholders` : Whether PagingData can display null placeholders or not.
  - PagingData displays null placeholders for content not yet loaded if two conditions are met.
  - When you can count all the unloaded items (when know how many nulls to display).
  - When enablePlaceholders is set to true. *default value is true*.
- initialLoadSize : PagingSource's initial load size is 3 * page size.
  - DEFAULT_INITIAL_PAGE_MULTIPLIER = 3.
  - This is to show sufficient items *when first loaded*.
  - Passed to the *first load()* call.
- maxSize : Maximum number of items that can be loaded into PagingData before deleting a page
  - MAX_SIZE_UNBOUNDED = Int.MAX_VALUE
  - Therefore, pages are not deleted and old pages are cached.
  - Minimum is pageSize + prefetchDistance * 2.
  - To prevent the load from being constantly called or discarded.
<br>

#### 3-2-2. cachedIn()
- Caches the *PagingData* such that any downstream collection from this flow will share.
- The coroutine scope where this page cache will be kept alive.
  - The flow is kept active as long as the given scope is active.
  - To avoid leaks, make sure to use a scope that is already managed or manually cancel it.
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    repository: PokemonRepositoryImpl
) : ViewModel() {
    val pokemonList = repository.getPokemonList().cachedIn(viewModelScope)
}
```
<br>

#### 3-3. PagingData
- Container for Paged data from a single generation of loads.
- PagingData can grow as it loads more data, but it cannot be updated.
<br>

#### 3-4. PagingDataAdapter
- `PagingDataAdapter` is abstract class extending *RecyclerView.Adapter*.
- `PagingDataAdapter` is an adapter for connecting *PagingData* and *RecyclerView*.
  - `PagingDataAdapter` handles paginated data.
- Most methods use [AsyncPagingDataDiffer](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:paging/paging-runtime/src/main/java/androidx/paging/AsyncPagingDataDiffer.kt) and [PagingDataDiffer](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:paging/paging-common/src/main/kotlin/androidx/paging/PagingDataDiffer.kt)
```kotlin
abstract class PagingDataAdapter<T : Any, VH : RecyclerView.ViewHolder> @JvmOverloads constructor(
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) : RecyclerView.Adapter<VH>() {
    ...
    private val differ = AsyncPagingDataDiffer(
        diffCallback = diffCallback,
        updateCallback = AdapterListUpdateCallback(this),
        mainDispatcher = mainDispatcher,
        workerDispatcher = workerDispatcher
    )
    ...
    suspend fun submitData(pagingData: PagingData<T>) {
        differ.submitData(pagingData)
    }
    ...
    fun retry() {
        differ.retry()
    }
    ...
    fun refresh() {
        differ.refresh()
    }
    ...
    protected fun getItem(@IntRange(from = 0) position: Int) = differ.getItem(position)
    ...
}
```
<br>

#### 3-4-1. Implement PagingDataAdapter
- Override *onCreateViewHolder()* and *onBindViewHolder()* methods like any *RecyclerView.Adapter*.
- Because that item may be null, ViewHolder must support binding a null item as a placeholder.
```kotlin
class MainAdapter :
    PagingDataAdapter<PokemonEntity, MainAdapter.PokemonViewHolder>(pokemonDiffUtil){
        
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPokemonBinding.inflate(inflater)
        return PokemonViewHolder(binding, move)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
```
- PagingDataAdapter also need [DiffUtil.ItemCallback](https://github.com/kym1924/listadapter-diffutil/blob/main/README.md#3-3-diffutilitemcallbackt).

```kotlin
companion object {
    private val pokemonDiffUtil = object : DiffUtil.ItemCallback<PokemonEntity>() {
        override fun areItemsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity) =
            oldItem.id() == newItem.id()

        override fun areContentsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity) =
            oldItem == newItem
    }
}
```
<br>

#### 3-4-2. Display the paged data in UI
- Collect Flow<PagingData&lt;Value>> and call *PagingDataAdapter.submitData()*.
  - Activity use lifecycleScope.
  - Fragment use viewLifecycleOwner.lifecycleScope.
```kotlin
lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.pokemonList.collectLatest {
            pokemonAdapter.submitData(it)
        }
    }
}
```
<br>

#### 3-5. AsyncPagingDataDiffer
- Helper class for mapping a *PagingData* into a *RecyclerView.Adapter*.
- For simplicity, can use *PagingDataAdapter* instead of `AsyncPagingDataDiffer`.
```kotlin
class AsyncPagingDataDiffer<T : Any> @JvmOverloads constructor(
    private val diffCallback: DiffUtil.ItemCallback<T>,
    @Suppress("ListenerLast") // have to suppress for each, due to defaults / JvmOverloads
    private val updateCallback: ListUpdateCallback,
    @Suppress("ListenerLast") // have to suppress for each, due to defaults / JvmOverloads
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    @Suppress("ListenerLast") // have to suppress for each, due to defaults / JvmOverloads
    private val workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    ...
    private val differBase = object : PagingDataDiffer<T>(differCallback, mainDispatcher) {
        ...
    }
    ...
    suspend fun submitData(pagingData: PagingData<T>) {
        submitDataId.incrementAndGet()
        differBase.collectFrom(pagingData)
    }
    ...
    fun retry() {
        differBase.retry()
    }
    ...
    fun refresh() {
        differBase.refresh()
    }
    ...
}
```
- The following methods are called in the following order.
    - submitData() : PagingDataAdapter - AsyncPagingDataDiffer - PagingDataDiffer.collectFrom()
    - retry() : PagingDataAdapter - AsyncPagingDataDiffer - PagingDataDiffer - UiReceiver
    - refresh() : PagingDataAdapter - AsyncPagingDataDiffer - PagingDataDiffer - UiReceiver
<br>

#### 3-6. UiReceiver
- *Initialize with pagingData.receiver in collectFrom() method of PagingDataDiffer*.
```kotlin
internal interface UiReceiver {
    fun accessHint(viewportHint: ViewportHint)
    fun retry()
    fun refresh()
}
```
<br>

##### Reference

- https://developer.android.com/topic/libraries/architecture/paging/v3-overview
- https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data
- https://developer.android.com/reference/kotlin/androidx/paging/Pager
- https://developer.android.com/reference/kotlin/androidx/paging/PagingConfig
- https://developer.android.com/reference/kotlin/androidx/paging/PagingData
- https://developer.android.com/reference/kotlin/androidx/paging/PagingSource
- https://developer.android.com/reference/kotlin/androidx/paging/PagingSource.LoadParams
- https://developer.android.com/reference/kotlin/androidx/paging/PagingSource.LoadResult
- https://developer.android.com/reference/kotlin/androidx/paging/AsyncPagingDataDiffer
- https://developer.android.com/codelabs/android-paging#0
- https://github.com/googlecodelabs/android-paging
- https://github.com/android/architecture-components-samples/tree/main/PagingSample
- https://github.com/android/architecture-components-samples/tree/main/PagingWithNetworkSample
- https://medium.com/androiddevelopers/introduction-to-paging-3-0-in-the-mad-skills-series-648f77231121
- https://medium.com/androiddevelopers/fetching-data-and-binding-it-to-the-ui-in-the-mad-skills-series-cea89868b3e1
