package com.romix.feature.feat96

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat96Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat96UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat96FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat96UserSummary
)

data class Feat96UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat96NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat96Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat96Config = Feat96Config()
) {

    fun loadSnapshot(userId: Long): Feat96NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat96NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat96UserSummary {
        return Feat96UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat96FeedItem> {
        val result = java.util.ArrayList<Feat96FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat96FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat96UiMapper {

    fun mapToUi(model: List<Feat96FeedItem>): Feat96UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat96UiModel(
            header = UiText("Feat96 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat96UiModel =
        Feat96UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat96UiModel =
        Feat96UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat96UiModel =
        Feat96UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat96Service(
    private val repository: Feat96Repository,
    private val uiMapper: Feat96UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat96UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat96UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat96UserItem1(val user: CoreUser, val label: String)
data class Feat96UserItem2(val user: CoreUser, val label: String)
data class Feat96UserItem3(val user: CoreUser, val label: String)
data class Feat96UserItem4(val user: CoreUser, val label: String)
data class Feat96UserItem5(val user: CoreUser, val label: String)
data class Feat96UserItem6(val user: CoreUser, val label: String)
data class Feat96UserItem7(val user: CoreUser, val label: String)
data class Feat96UserItem8(val user: CoreUser, val label: String)
data class Feat96UserItem9(val user: CoreUser, val label: String)
data class Feat96UserItem10(val user: CoreUser, val label: String)

data class Feat96StateBlock1(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock2(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock3(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock4(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock5(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock6(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock7(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock8(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock9(val state: Feat96UiModel, val checksum: Int)
data class Feat96StateBlock10(val state: Feat96UiModel, val checksum: Int)

fun buildFeat96UserItem(user: CoreUser, index: Int): Feat96UserItem1 {
    return Feat96UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat96StateBlock(model: Feat96UiModel): Feat96StateBlock1 {
    return Feat96StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat96UserSummary> {
    val list = java.util.ArrayList<Feat96UserSummary>(users.size)
    for (user in users) {
        list += Feat96UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat96UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat96UiModel {
    val summaries = (0 until count).map {
        Feat96UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat96UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat96UiModel> {
    val models = java.util.ArrayList<Feat96UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat96AnalyticsEvent1(val name: String, val value: String)
data class Feat96AnalyticsEvent2(val name: String, val value: String)
data class Feat96AnalyticsEvent3(val name: String, val value: String)
data class Feat96AnalyticsEvent4(val name: String, val value: String)
data class Feat96AnalyticsEvent5(val name: String, val value: String)
data class Feat96AnalyticsEvent6(val name: String, val value: String)
data class Feat96AnalyticsEvent7(val name: String, val value: String)
data class Feat96AnalyticsEvent8(val name: String, val value: String)
data class Feat96AnalyticsEvent9(val name: String, val value: String)
data class Feat96AnalyticsEvent10(val name: String, val value: String)

fun logFeat96Event1(event: Feat96AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat96Event2(event: Feat96AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat96Event3(event: Feat96AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat96Event4(event: Feat96AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat96Event5(event: Feat96AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat96Event6(event: Feat96AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat96Event7(event: Feat96AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat96Event8(event: Feat96AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat96Event9(event: Feat96AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat96Event10(event: Feat96AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat96Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat96Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat96(u: CoreUser): Feat96Projection1 =
    Feat96Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat96Projection1> {
    val list = java.util.ArrayList<Feat96Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat96(u)
    }
    return list
}
