package com.romix.feature.feat424

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat424Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat424UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat424FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat424UserSummary
)

data class Feat424UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat424NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat424Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat424Config = Feat424Config()
) {

    fun loadSnapshot(userId: Long): Feat424NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat424NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat424UserSummary {
        return Feat424UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat424FeedItem> {
        val result = java.util.ArrayList<Feat424FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat424FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat424UiMapper {

    fun mapToUi(model: List<Feat424FeedItem>): Feat424UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat424UiModel(
            header = UiText("Feat424 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat424UiModel =
        Feat424UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat424UiModel =
        Feat424UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat424UiModel =
        Feat424UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat424Service(
    private val repository: Feat424Repository,
    private val uiMapper: Feat424UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat424UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat424UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat424UserItem1(val user: CoreUser, val label: String)
data class Feat424UserItem2(val user: CoreUser, val label: String)
data class Feat424UserItem3(val user: CoreUser, val label: String)
data class Feat424UserItem4(val user: CoreUser, val label: String)
data class Feat424UserItem5(val user: CoreUser, val label: String)
data class Feat424UserItem6(val user: CoreUser, val label: String)
data class Feat424UserItem7(val user: CoreUser, val label: String)
data class Feat424UserItem8(val user: CoreUser, val label: String)
data class Feat424UserItem9(val user: CoreUser, val label: String)
data class Feat424UserItem10(val user: CoreUser, val label: String)

data class Feat424StateBlock1(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock2(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock3(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock4(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock5(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock6(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock7(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock8(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock9(val state: Feat424UiModel, val checksum: Int)
data class Feat424StateBlock10(val state: Feat424UiModel, val checksum: Int)

fun buildFeat424UserItem(user: CoreUser, index: Int): Feat424UserItem1 {
    return Feat424UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat424StateBlock(model: Feat424UiModel): Feat424StateBlock1 {
    return Feat424StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat424UserSummary> {
    val list = java.util.ArrayList<Feat424UserSummary>(users.size)
    for (user in users) {
        list += Feat424UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat424UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat424UiModel {
    val summaries = (0 until count).map {
        Feat424UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat424UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat424UiModel> {
    val models = java.util.ArrayList<Feat424UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat424AnalyticsEvent1(val name: String, val value: String)
data class Feat424AnalyticsEvent2(val name: String, val value: String)
data class Feat424AnalyticsEvent3(val name: String, val value: String)
data class Feat424AnalyticsEvent4(val name: String, val value: String)
data class Feat424AnalyticsEvent5(val name: String, val value: String)
data class Feat424AnalyticsEvent6(val name: String, val value: String)
data class Feat424AnalyticsEvent7(val name: String, val value: String)
data class Feat424AnalyticsEvent8(val name: String, val value: String)
data class Feat424AnalyticsEvent9(val name: String, val value: String)
data class Feat424AnalyticsEvent10(val name: String, val value: String)

fun logFeat424Event1(event: Feat424AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat424Event2(event: Feat424AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat424Event3(event: Feat424AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat424Event4(event: Feat424AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat424Event5(event: Feat424AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat424Event6(event: Feat424AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat424Event7(event: Feat424AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat424Event8(event: Feat424AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat424Event9(event: Feat424AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat424Event10(event: Feat424AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat424Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat424Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat424(u: CoreUser): Feat424Projection1 =
    Feat424Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat424Projection1> {
    val list = java.util.ArrayList<Feat424Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat424(u)
    }
    return list
}
