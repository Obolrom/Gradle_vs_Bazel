package com.romix.feature.feat470

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat470Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat470UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat470FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat470UserSummary
)

data class Feat470UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat470NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat470Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat470Config = Feat470Config()
) {

    fun loadSnapshot(userId: Long): Feat470NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat470NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat470UserSummary {
        return Feat470UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat470FeedItem> {
        val result = java.util.ArrayList<Feat470FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat470FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat470UiMapper {

    fun mapToUi(model: List<Feat470FeedItem>): Feat470UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat470UiModel(
            header = UiText("Feat470 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat470UiModel =
        Feat470UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat470UiModel =
        Feat470UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat470UiModel =
        Feat470UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat470Service(
    private val repository: Feat470Repository,
    private val uiMapper: Feat470UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat470UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat470UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat470UserItem1(val user: CoreUser, val label: String)
data class Feat470UserItem2(val user: CoreUser, val label: String)
data class Feat470UserItem3(val user: CoreUser, val label: String)
data class Feat470UserItem4(val user: CoreUser, val label: String)
data class Feat470UserItem5(val user: CoreUser, val label: String)
data class Feat470UserItem6(val user: CoreUser, val label: String)
data class Feat470UserItem7(val user: CoreUser, val label: String)
data class Feat470UserItem8(val user: CoreUser, val label: String)
data class Feat470UserItem9(val user: CoreUser, val label: String)
data class Feat470UserItem10(val user: CoreUser, val label: String)

data class Feat470StateBlock1(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock2(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock3(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock4(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock5(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock6(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock7(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock8(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock9(val state: Feat470UiModel, val checksum: Int)
data class Feat470StateBlock10(val state: Feat470UiModel, val checksum: Int)

fun buildFeat470UserItem(user: CoreUser, index: Int): Feat470UserItem1 {
    return Feat470UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat470StateBlock(model: Feat470UiModel): Feat470StateBlock1 {
    return Feat470StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat470UserSummary> {
    val list = java.util.ArrayList<Feat470UserSummary>(users.size)
    for (user in users) {
        list += Feat470UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat470UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat470UiModel {
    val summaries = (0 until count).map {
        Feat470UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat470UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat470UiModel> {
    val models = java.util.ArrayList<Feat470UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat470AnalyticsEvent1(val name: String, val value: String)
data class Feat470AnalyticsEvent2(val name: String, val value: String)
data class Feat470AnalyticsEvent3(val name: String, val value: String)
data class Feat470AnalyticsEvent4(val name: String, val value: String)
data class Feat470AnalyticsEvent5(val name: String, val value: String)
data class Feat470AnalyticsEvent6(val name: String, val value: String)
data class Feat470AnalyticsEvent7(val name: String, val value: String)
data class Feat470AnalyticsEvent8(val name: String, val value: String)
data class Feat470AnalyticsEvent9(val name: String, val value: String)
data class Feat470AnalyticsEvent10(val name: String, val value: String)

fun logFeat470Event1(event: Feat470AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat470Event2(event: Feat470AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat470Event3(event: Feat470AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat470Event4(event: Feat470AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat470Event5(event: Feat470AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat470Event6(event: Feat470AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat470Event7(event: Feat470AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat470Event8(event: Feat470AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat470Event9(event: Feat470AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat470Event10(event: Feat470AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat470Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat470Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat470(u: CoreUser): Feat470Projection1 =
    Feat470Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat470Projection1> {
    val list = java.util.ArrayList<Feat470Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat470(u)
    }
    return list
}
