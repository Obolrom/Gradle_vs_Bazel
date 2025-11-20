package com.romix.feature.feat381

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat381Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat381UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat381FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat381UserSummary
)

data class Feat381UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat381NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat381Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat381Config = Feat381Config()
) {

    fun loadSnapshot(userId: Long): Feat381NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat381NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat381UserSummary {
        return Feat381UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat381FeedItem> {
        val result = java.util.ArrayList<Feat381FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat381FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat381UiMapper {

    fun mapToUi(model: List<Feat381FeedItem>): Feat381UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat381UiModel(
            header = UiText("Feat381 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat381UiModel =
        Feat381UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat381UiModel =
        Feat381UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat381UiModel =
        Feat381UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat381Service(
    private val repository: Feat381Repository,
    private val uiMapper: Feat381UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat381UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat381UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat381UserItem1(val user: CoreUser, val label: String)
data class Feat381UserItem2(val user: CoreUser, val label: String)
data class Feat381UserItem3(val user: CoreUser, val label: String)
data class Feat381UserItem4(val user: CoreUser, val label: String)
data class Feat381UserItem5(val user: CoreUser, val label: String)
data class Feat381UserItem6(val user: CoreUser, val label: String)
data class Feat381UserItem7(val user: CoreUser, val label: String)
data class Feat381UserItem8(val user: CoreUser, val label: String)
data class Feat381UserItem9(val user: CoreUser, val label: String)
data class Feat381UserItem10(val user: CoreUser, val label: String)

data class Feat381StateBlock1(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock2(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock3(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock4(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock5(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock6(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock7(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock8(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock9(val state: Feat381UiModel, val checksum: Int)
data class Feat381StateBlock10(val state: Feat381UiModel, val checksum: Int)

fun buildFeat381UserItem(user: CoreUser, index: Int): Feat381UserItem1 {
    return Feat381UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat381StateBlock(model: Feat381UiModel): Feat381StateBlock1 {
    return Feat381StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat381UserSummary> {
    val list = java.util.ArrayList<Feat381UserSummary>(users.size)
    for (user in users) {
        list += Feat381UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat381UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat381UiModel {
    val summaries = (0 until count).map {
        Feat381UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat381UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat381UiModel> {
    val models = java.util.ArrayList<Feat381UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat381AnalyticsEvent1(val name: String, val value: String)
data class Feat381AnalyticsEvent2(val name: String, val value: String)
data class Feat381AnalyticsEvent3(val name: String, val value: String)
data class Feat381AnalyticsEvent4(val name: String, val value: String)
data class Feat381AnalyticsEvent5(val name: String, val value: String)
data class Feat381AnalyticsEvent6(val name: String, val value: String)
data class Feat381AnalyticsEvent7(val name: String, val value: String)
data class Feat381AnalyticsEvent8(val name: String, val value: String)
data class Feat381AnalyticsEvent9(val name: String, val value: String)
data class Feat381AnalyticsEvent10(val name: String, val value: String)

fun logFeat381Event1(event: Feat381AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat381Event2(event: Feat381AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat381Event3(event: Feat381AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat381Event4(event: Feat381AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat381Event5(event: Feat381AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat381Event6(event: Feat381AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat381Event7(event: Feat381AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat381Event8(event: Feat381AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat381Event9(event: Feat381AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat381Event10(event: Feat381AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat381Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat381Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat381(u: CoreUser): Feat381Projection1 =
    Feat381Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat381Projection1> {
    val list = java.util.ArrayList<Feat381Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat381(u)
    }
    return list
}
