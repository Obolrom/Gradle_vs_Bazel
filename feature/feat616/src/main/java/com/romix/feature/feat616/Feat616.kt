package com.romix.feature.feat616

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat616Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat616UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat616FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat616UserSummary
)

data class Feat616UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat616NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat616Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat616Config = Feat616Config()
) {

    fun loadSnapshot(userId: Long): Feat616NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat616NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat616UserSummary {
        return Feat616UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat616FeedItem> {
        val result = java.util.ArrayList<Feat616FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat616FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat616UiMapper {

    fun mapToUi(model: List<Feat616FeedItem>): Feat616UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat616UiModel(
            header = UiText("Feat616 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat616UiModel =
        Feat616UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat616UiModel =
        Feat616UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat616UiModel =
        Feat616UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat616Service(
    private val repository: Feat616Repository,
    private val uiMapper: Feat616UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat616UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat616UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat616UserItem1(val user: CoreUser, val label: String)
data class Feat616UserItem2(val user: CoreUser, val label: String)
data class Feat616UserItem3(val user: CoreUser, val label: String)
data class Feat616UserItem4(val user: CoreUser, val label: String)
data class Feat616UserItem5(val user: CoreUser, val label: String)
data class Feat616UserItem6(val user: CoreUser, val label: String)
data class Feat616UserItem7(val user: CoreUser, val label: String)
data class Feat616UserItem8(val user: CoreUser, val label: String)
data class Feat616UserItem9(val user: CoreUser, val label: String)
data class Feat616UserItem10(val user: CoreUser, val label: String)

data class Feat616StateBlock1(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock2(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock3(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock4(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock5(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock6(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock7(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock8(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock9(val state: Feat616UiModel, val checksum: Int)
data class Feat616StateBlock10(val state: Feat616UiModel, val checksum: Int)

fun buildFeat616UserItem(user: CoreUser, index: Int): Feat616UserItem1 {
    return Feat616UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat616StateBlock(model: Feat616UiModel): Feat616StateBlock1 {
    return Feat616StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat616UserSummary> {
    val list = java.util.ArrayList<Feat616UserSummary>(users.size)
    for (user in users) {
        list += Feat616UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat616UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat616UiModel {
    val summaries = (0 until count).map {
        Feat616UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat616UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat616UiModel> {
    val models = java.util.ArrayList<Feat616UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat616AnalyticsEvent1(val name: String, val value: String)
data class Feat616AnalyticsEvent2(val name: String, val value: String)
data class Feat616AnalyticsEvent3(val name: String, val value: String)
data class Feat616AnalyticsEvent4(val name: String, val value: String)
data class Feat616AnalyticsEvent5(val name: String, val value: String)
data class Feat616AnalyticsEvent6(val name: String, val value: String)
data class Feat616AnalyticsEvent7(val name: String, val value: String)
data class Feat616AnalyticsEvent8(val name: String, val value: String)
data class Feat616AnalyticsEvent9(val name: String, val value: String)
data class Feat616AnalyticsEvent10(val name: String, val value: String)

fun logFeat616Event1(event: Feat616AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat616Event2(event: Feat616AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat616Event3(event: Feat616AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat616Event4(event: Feat616AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat616Event5(event: Feat616AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat616Event6(event: Feat616AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat616Event7(event: Feat616AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat616Event8(event: Feat616AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat616Event9(event: Feat616AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat616Event10(event: Feat616AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat616Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat616Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat616(u: CoreUser): Feat616Projection1 =
    Feat616Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat616Projection1> {
    val list = java.util.ArrayList<Feat616Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat616(u)
    }
    return list
}
