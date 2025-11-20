package com.romix.feature.feat428

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat428Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat428UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat428FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat428UserSummary
)

data class Feat428UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat428NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat428Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat428Config = Feat428Config()
) {

    fun loadSnapshot(userId: Long): Feat428NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat428NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat428UserSummary {
        return Feat428UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat428FeedItem> {
        val result = java.util.ArrayList<Feat428FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat428FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat428UiMapper {

    fun mapToUi(model: List<Feat428FeedItem>): Feat428UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat428UiModel(
            header = UiText("Feat428 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat428UiModel =
        Feat428UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat428UiModel =
        Feat428UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat428UiModel =
        Feat428UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat428Service(
    private val repository: Feat428Repository,
    private val uiMapper: Feat428UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat428UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat428UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat428UserItem1(val user: CoreUser, val label: String)
data class Feat428UserItem2(val user: CoreUser, val label: String)
data class Feat428UserItem3(val user: CoreUser, val label: String)
data class Feat428UserItem4(val user: CoreUser, val label: String)
data class Feat428UserItem5(val user: CoreUser, val label: String)
data class Feat428UserItem6(val user: CoreUser, val label: String)
data class Feat428UserItem7(val user: CoreUser, val label: String)
data class Feat428UserItem8(val user: CoreUser, val label: String)
data class Feat428UserItem9(val user: CoreUser, val label: String)
data class Feat428UserItem10(val user: CoreUser, val label: String)

data class Feat428StateBlock1(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock2(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock3(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock4(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock5(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock6(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock7(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock8(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock9(val state: Feat428UiModel, val checksum: Int)
data class Feat428StateBlock10(val state: Feat428UiModel, val checksum: Int)

fun buildFeat428UserItem(user: CoreUser, index: Int): Feat428UserItem1 {
    return Feat428UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat428StateBlock(model: Feat428UiModel): Feat428StateBlock1 {
    return Feat428StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat428UserSummary> {
    val list = java.util.ArrayList<Feat428UserSummary>(users.size)
    for (user in users) {
        list += Feat428UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat428UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat428UiModel {
    val summaries = (0 until count).map {
        Feat428UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat428UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat428UiModel> {
    val models = java.util.ArrayList<Feat428UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat428AnalyticsEvent1(val name: String, val value: String)
data class Feat428AnalyticsEvent2(val name: String, val value: String)
data class Feat428AnalyticsEvent3(val name: String, val value: String)
data class Feat428AnalyticsEvent4(val name: String, val value: String)
data class Feat428AnalyticsEvent5(val name: String, val value: String)
data class Feat428AnalyticsEvent6(val name: String, val value: String)
data class Feat428AnalyticsEvent7(val name: String, val value: String)
data class Feat428AnalyticsEvent8(val name: String, val value: String)
data class Feat428AnalyticsEvent9(val name: String, val value: String)
data class Feat428AnalyticsEvent10(val name: String, val value: String)

fun logFeat428Event1(event: Feat428AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat428Event2(event: Feat428AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat428Event3(event: Feat428AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat428Event4(event: Feat428AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat428Event5(event: Feat428AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat428Event6(event: Feat428AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat428Event7(event: Feat428AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat428Event8(event: Feat428AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat428Event9(event: Feat428AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat428Event10(event: Feat428AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat428Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat428Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat428(u: CoreUser): Feat428Projection1 =
    Feat428Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat428Projection1> {
    val list = java.util.ArrayList<Feat428Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat428(u)
    }
    return list
}
