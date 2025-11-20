package com.romix.feature.feat489

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat489Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat489UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat489FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat489UserSummary
)

data class Feat489UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat489NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat489Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat489Config = Feat489Config()
) {

    fun loadSnapshot(userId: Long): Feat489NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat489NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat489UserSummary {
        return Feat489UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat489FeedItem> {
        val result = java.util.ArrayList<Feat489FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat489FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat489UiMapper {

    fun mapToUi(model: List<Feat489FeedItem>): Feat489UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat489UiModel(
            header = UiText("Feat489 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat489UiModel =
        Feat489UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat489UiModel =
        Feat489UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat489UiModel =
        Feat489UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat489Service(
    private val repository: Feat489Repository,
    private val uiMapper: Feat489UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat489UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat489UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat489UserItem1(val user: CoreUser, val label: String)
data class Feat489UserItem2(val user: CoreUser, val label: String)
data class Feat489UserItem3(val user: CoreUser, val label: String)
data class Feat489UserItem4(val user: CoreUser, val label: String)
data class Feat489UserItem5(val user: CoreUser, val label: String)
data class Feat489UserItem6(val user: CoreUser, val label: String)
data class Feat489UserItem7(val user: CoreUser, val label: String)
data class Feat489UserItem8(val user: CoreUser, val label: String)
data class Feat489UserItem9(val user: CoreUser, val label: String)
data class Feat489UserItem10(val user: CoreUser, val label: String)

data class Feat489StateBlock1(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock2(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock3(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock4(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock5(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock6(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock7(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock8(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock9(val state: Feat489UiModel, val checksum: Int)
data class Feat489StateBlock10(val state: Feat489UiModel, val checksum: Int)

fun buildFeat489UserItem(user: CoreUser, index: Int): Feat489UserItem1 {
    return Feat489UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat489StateBlock(model: Feat489UiModel): Feat489StateBlock1 {
    return Feat489StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat489UserSummary> {
    val list = java.util.ArrayList<Feat489UserSummary>(users.size)
    for (user in users) {
        list += Feat489UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat489UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat489UiModel {
    val summaries = (0 until count).map {
        Feat489UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat489UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat489UiModel> {
    val models = java.util.ArrayList<Feat489UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat489AnalyticsEvent1(val name: String, val value: String)
data class Feat489AnalyticsEvent2(val name: String, val value: String)
data class Feat489AnalyticsEvent3(val name: String, val value: String)
data class Feat489AnalyticsEvent4(val name: String, val value: String)
data class Feat489AnalyticsEvent5(val name: String, val value: String)
data class Feat489AnalyticsEvent6(val name: String, val value: String)
data class Feat489AnalyticsEvent7(val name: String, val value: String)
data class Feat489AnalyticsEvent8(val name: String, val value: String)
data class Feat489AnalyticsEvent9(val name: String, val value: String)
data class Feat489AnalyticsEvent10(val name: String, val value: String)

fun logFeat489Event1(event: Feat489AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat489Event2(event: Feat489AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat489Event3(event: Feat489AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat489Event4(event: Feat489AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat489Event5(event: Feat489AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat489Event6(event: Feat489AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat489Event7(event: Feat489AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat489Event8(event: Feat489AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat489Event9(event: Feat489AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat489Event10(event: Feat489AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat489Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat489Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat489(u: CoreUser): Feat489Projection1 =
    Feat489Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat489Projection1> {
    val list = java.util.ArrayList<Feat489Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat489(u)
    }
    return list
}
