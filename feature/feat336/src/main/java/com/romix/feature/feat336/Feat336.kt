package com.romix.feature.feat336

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat336Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat336UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat336FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat336UserSummary
)

data class Feat336UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat336NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat336Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat336Config = Feat336Config()
) {

    fun loadSnapshot(userId: Long): Feat336NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat336NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat336UserSummary {
        return Feat336UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat336FeedItem> {
        val result = java.util.ArrayList<Feat336FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat336FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat336UiMapper {

    fun mapToUi(model: List<Feat336FeedItem>): Feat336UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat336UiModel(
            header = UiText("Feat336 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat336UiModel =
        Feat336UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat336UiModel =
        Feat336UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat336UiModel =
        Feat336UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat336Service(
    private val repository: Feat336Repository,
    private val uiMapper: Feat336UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat336UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat336UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat336UserItem1(val user: CoreUser, val label: String)
data class Feat336UserItem2(val user: CoreUser, val label: String)
data class Feat336UserItem3(val user: CoreUser, val label: String)
data class Feat336UserItem4(val user: CoreUser, val label: String)
data class Feat336UserItem5(val user: CoreUser, val label: String)
data class Feat336UserItem6(val user: CoreUser, val label: String)
data class Feat336UserItem7(val user: CoreUser, val label: String)
data class Feat336UserItem8(val user: CoreUser, val label: String)
data class Feat336UserItem9(val user: CoreUser, val label: String)
data class Feat336UserItem10(val user: CoreUser, val label: String)

data class Feat336StateBlock1(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock2(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock3(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock4(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock5(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock6(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock7(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock8(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock9(val state: Feat336UiModel, val checksum: Int)
data class Feat336StateBlock10(val state: Feat336UiModel, val checksum: Int)

fun buildFeat336UserItem(user: CoreUser, index: Int): Feat336UserItem1 {
    return Feat336UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat336StateBlock(model: Feat336UiModel): Feat336StateBlock1 {
    return Feat336StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat336UserSummary> {
    val list = java.util.ArrayList<Feat336UserSummary>(users.size)
    for (user in users) {
        list += Feat336UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat336UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat336UiModel {
    val summaries = (0 until count).map {
        Feat336UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat336UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat336UiModel> {
    val models = java.util.ArrayList<Feat336UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat336AnalyticsEvent1(val name: String, val value: String)
data class Feat336AnalyticsEvent2(val name: String, val value: String)
data class Feat336AnalyticsEvent3(val name: String, val value: String)
data class Feat336AnalyticsEvent4(val name: String, val value: String)
data class Feat336AnalyticsEvent5(val name: String, val value: String)
data class Feat336AnalyticsEvent6(val name: String, val value: String)
data class Feat336AnalyticsEvent7(val name: String, val value: String)
data class Feat336AnalyticsEvent8(val name: String, val value: String)
data class Feat336AnalyticsEvent9(val name: String, val value: String)
data class Feat336AnalyticsEvent10(val name: String, val value: String)

fun logFeat336Event1(event: Feat336AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat336Event2(event: Feat336AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat336Event3(event: Feat336AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat336Event4(event: Feat336AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat336Event5(event: Feat336AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat336Event6(event: Feat336AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat336Event7(event: Feat336AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat336Event8(event: Feat336AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat336Event9(event: Feat336AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat336Event10(event: Feat336AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat336Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat336Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat336(u: CoreUser): Feat336Projection1 =
    Feat336Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat336Projection1> {
    val list = java.util.ArrayList<Feat336Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat336(u)
    }
    return list
}
