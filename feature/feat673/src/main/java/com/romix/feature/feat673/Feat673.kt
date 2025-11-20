package com.romix.feature.feat673

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat673Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat673UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat673FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat673UserSummary
)

data class Feat673UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat673NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat673Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat673Config = Feat673Config()
) {

    fun loadSnapshot(userId: Long): Feat673NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat673NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat673UserSummary {
        return Feat673UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat673FeedItem> {
        val result = java.util.ArrayList<Feat673FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat673FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat673UiMapper {

    fun mapToUi(model: List<Feat673FeedItem>): Feat673UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat673UiModel(
            header = UiText("Feat673 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat673UiModel =
        Feat673UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat673UiModel =
        Feat673UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat673UiModel =
        Feat673UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat673Service(
    private val repository: Feat673Repository,
    private val uiMapper: Feat673UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat673UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat673UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat673UserItem1(val user: CoreUser, val label: String)
data class Feat673UserItem2(val user: CoreUser, val label: String)
data class Feat673UserItem3(val user: CoreUser, val label: String)
data class Feat673UserItem4(val user: CoreUser, val label: String)
data class Feat673UserItem5(val user: CoreUser, val label: String)
data class Feat673UserItem6(val user: CoreUser, val label: String)
data class Feat673UserItem7(val user: CoreUser, val label: String)
data class Feat673UserItem8(val user: CoreUser, val label: String)
data class Feat673UserItem9(val user: CoreUser, val label: String)
data class Feat673UserItem10(val user: CoreUser, val label: String)

data class Feat673StateBlock1(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock2(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock3(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock4(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock5(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock6(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock7(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock8(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock9(val state: Feat673UiModel, val checksum: Int)
data class Feat673StateBlock10(val state: Feat673UiModel, val checksum: Int)

fun buildFeat673UserItem(user: CoreUser, index: Int): Feat673UserItem1 {
    return Feat673UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat673StateBlock(model: Feat673UiModel): Feat673StateBlock1 {
    return Feat673StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat673UserSummary> {
    val list = java.util.ArrayList<Feat673UserSummary>(users.size)
    for (user in users) {
        list += Feat673UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat673UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat673UiModel {
    val summaries = (0 until count).map {
        Feat673UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat673UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat673UiModel> {
    val models = java.util.ArrayList<Feat673UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat673AnalyticsEvent1(val name: String, val value: String)
data class Feat673AnalyticsEvent2(val name: String, val value: String)
data class Feat673AnalyticsEvent3(val name: String, val value: String)
data class Feat673AnalyticsEvent4(val name: String, val value: String)
data class Feat673AnalyticsEvent5(val name: String, val value: String)
data class Feat673AnalyticsEvent6(val name: String, val value: String)
data class Feat673AnalyticsEvent7(val name: String, val value: String)
data class Feat673AnalyticsEvent8(val name: String, val value: String)
data class Feat673AnalyticsEvent9(val name: String, val value: String)
data class Feat673AnalyticsEvent10(val name: String, val value: String)

fun logFeat673Event1(event: Feat673AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat673Event2(event: Feat673AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat673Event3(event: Feat673AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat673Event4(event: Feat673AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat673Event5(event: Feat673AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat673Event6(event: Feat673AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat673Event7(event: Feat673AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat673Event8(event: Feat673AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat673Event9(event: Feat673AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat673Event10(event: Feat673AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat673Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat673Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat673(u: CoreUser): Feat673Projection1 =
    Feat673Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat673Projection1> {
    val list = java.util.ArrayList<Feat673Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat673(u)
    }
    return list
}
