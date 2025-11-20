package com.romix.feature.feat584

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat584Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat584UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat584FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat584UserSummary
)

data class Feat584UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat584NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat584Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat584Config = Feat584Config()
) {

    fun loadSnapshot(userId: Long): Feat584NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat584NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat584UserSummary {
        return Feat584UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat584FeedItem> {
        val result = java.util.ArrayList<Feat584FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat584FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat584UiMapper {

    fun mapToUi(model: List<Feat584FeedItem>): Feat584UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat584UiModel(
            header = UiText("Feat584 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat584UiModel =
        Feat584UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat584UiModel =
        Feat584UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat584UiModel =
        Feat584UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat584Service(
    private val repository: Feat584Repository,
    private val uiMapper: Feat584UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat584UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat584UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat584UserItem1(val user: CoreUser, val label: String)
data class Feat584UserItem2(val user: CoreUser, val label: String)
data class Feat584UserItem3(val user: CoreUser, val label: String)
data class Feat584UserItem4(val user: CoreUser, val label: String)
data class Feat584UserItem5(val user: CoreUser, val label: String)
data class Feat584UserItem6(val user: CoreUser, val label: String)
data class Feat584UserItem7(val user: CoreUser, val label: String)
data class Feat584UserItem8(val user: CoreUser, val label: String)
data class Feat584UserItem9(val user: CoreUser, val label: String)
data class Feat584UserItem10(val user: CoreUser, val label: String)

data class Feat584StateBlock1(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock2(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock3(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock4(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock5(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock6(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock7(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock8(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock9(val state: Feat584UiModel, val checksum: Int)
data class Feat584StateBlock10(val state: Feat584UiModel, val checksum: Int)

fun buildFeat584UserItem(user: CoreUser, index: Int): Feat584UserItem1 {
    return Feat584UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat584StateBlock(model: Feat584UiModel): Feat584StateBlock1 {
    return Feat584StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat584UserSummary> {
    val list = java.util.ArrayList<Feat584UserSummary>(users.size)
    for (user in users) {
        list += Feat584UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat584UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat584UiModel {
    val summaries = (0 until count).map {
        Feat584UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat584UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat584UiModel> {
    val models = java.util.ArrayList<Feat584UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat584AnalyticsEvent1(val name: String, val value: String)
data class Feat584AnalyticsEvent2(val name: String, val value: String)
data class Feat584AnalyticsEvent3(val name: String, val value: String)
data class Feat584AnalyticsEvent4(val name: String, val value: String)
data class Feat584AnalyticsEvent5(val name: String, val value: String)
data class Feat584AnalyticsEvent6(val name: String, val value: String)
data class Feat584AnalyticsEvent7(val name: String, val value: String)
data class Feat584AnalyticsEvent8(val name: String, val value: String)
data class Feat584AnalyticsEvent9(val name: String, val value: String)
data class Feat584AnalyticsEvent10(val name: String, val value: String)

fun logFeat584Event1(event: Feat584AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat584Event2(event: Feat584AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat584Event3(event: Feat584AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat584Event4(event: Feat584AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat584Event5(event: Feat584AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat584Event6(event: Feat584AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat584Event7(event: Feat584AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat584Event8(event: Feat584AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat584Event9(event: Feat584AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat584Event10(event: Feat584AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat584Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat584Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat584(u: CoreUser): Feat584Projection1 =
    Feat584Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat584Projection1> {
    val list = java.util.ArrayList<Feat584Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat584(u)
    }
    return list
}
