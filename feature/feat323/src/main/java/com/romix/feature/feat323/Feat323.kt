package com.romix.feature.feat323

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat323Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat323UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat323FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat323UserSummary
)

data class Feat323UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat323NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat323Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat323Config = Feat323Config()
) {

    fun loadSnapshot(userId: Long): Feat323NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat323NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat323UserSummary {
        return Feat323UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat323FeedItem> {
        val result = java.util.ArrayList<Feat323FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat323FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat323UiMapper {

    fun mapToUi(model: List<Feat323FeedItem>): Feat323UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat323UiModel(
            header = UiText("Feat323 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat323UiModel =
        Feat323UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat323UiModel =
        Feat323UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat323UiModel =
        Feat323UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat323Service(
    private val repository: Feat323Repository,
    private val uiMapper: Feat323UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat323UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat323UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat323UserItem1(val user: CoreUser, val label: String)
data class Feat323UserItem2(val user: CoreUser, val label: String)
data class Feat323UserItem3(val user: CoreUser, val label: String)
data class Feat323UserItem4(val user: CoreUser, val label: String)
data class Feat323UserItem5(val user: CoreUser, val label: String)
data class Feat323UserItem6(val user: CoreUser, val label: String)
data class Feat323UserItem7(val user: CoreUser, val label: String)
data class Feat323UserItem8(val user: CoreUser, val label: String)
data class Feat323UserItem9(val user: CoreUser, val label: String)
data class Feat323UserItem10(val user: CoreUser, val label: String)

data class Feat323StateBlock1(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock2(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock3(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock4(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock5(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock6(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock7(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock8(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock9(val state: Feat323UiModel, val checksum: Int)
data class Feat323StateBlock10(val state: Feat323UiModel, val checksum: Int)

fun buildFeat323UserItem(user: CoreUser, index: Int): Feat323UserItem1 {
    return Feat323UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat323StateBlock(model: Feat323UiModel): Feat323StateBlock1 {
    return Feat323StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat323UserSummary> {
    val list = java.util.ArrayList<Feat323UserSummary>(users.size)
    for (user in users) {
        list += Feat323UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat323UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat323UiModel {
    val summaries = (0 until count).map {
        Feat323UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat323UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat323UiModel> {
    val models = java.util.ArrayList<Feat323UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat323AnalyticsEvent1(val name: String, val value: String)
data class Feat323AnalyticsEvent2(val name: String, val value: String)
data class Feat323AnalyticsEvent3(val name: String, val value: String)
data class Feat323AnalyticsEvent4(val name: String, val value: String)
data class Feat323AnalyticsEvent5(val name: String, val value: String)
data class Feat323AnalyticsEvent6(val name: String, val value: String)
data class Feat323AnalyticsEvent7(val name: String, val value: String)
data class Feat323AnalyticsEvent8(val name: String, val value: String)
data class Feat323AnalyticsEvent9(val name: String, val value: String)
data class Feat323AnalyticsEvent10(val name: String, val value: String)

fun logFeat323Event1(event: Feat323AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat323Event2(event: Feat323AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat323Event3(event: Feat323AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat323Event4(event: Feat323AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat323Event5(event: Feat323AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat323Event6(event: Feat323AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat323Event7(event: Feat323AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat323Event8(event: Feat323AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat323Event9(event: Feat323AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat323Event10(event: Feat323AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat323Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat323Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat323(u: CoreUser): Feat323Projection1 =
    Feat323Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat323Projection1> {
    val list = java.util.ArrayList<Feat323Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat323(u)
    }
    return list
}
