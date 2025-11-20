package com.romix.feature.feat675

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat675Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat675UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat675FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat675UserSummary
)

data class Feat675UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat675NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat675Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat675Config = Feat675Config()
) {

    fun loadSnapshot(userId: Long): Feat675NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat675NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat675UserSummary {
        return Feat675UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat675FeedItem> {
        val result = java.util.ArrayList<Feat675FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat675FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat675UiMapper {

    fun mapToUi(model: List<Feat675FeedItem>): Feat675UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat675UiModel(
            header = UiText("Feat675 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat675UiModel =
        Feat675UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat675UiModel =
        Feat675UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat675UiModel =
        Feat675UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat675Service(
    private val repository: Feat675Repository,
    private val uiMapper: Feat675UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat675UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat675UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat675UserItem1(val user: CoreUser, val label: String)
data class Feat675UserItem2(val user: CoreUser, val label: String)
data class Feat675UserItem3(val user: CoreUser, val label: String)
data class Feat675UserItem4(val user: CoreUser, val label: String)
data class Feat675UserItem5(val user: CoreUser, val label: String)
data class Feat675UserItem6(val user: CoreUser, val label: String)
data class Feat675UserItem7(val user: CoreUser, val label: String)
data class Feat675UserItem8(val user: CoreUser, val label: String)
data class Feat675UserItem9(val user: CoreUser, val label: String)
data class Feat675UserItem10(val user: CoreUser, val label: String)

data class Feat675StateBlock1(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock2(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock3(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock4(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock5(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock6(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock7(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock8(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock9(val state: Feat675UiModel, val checksum: Int)
data class Feat675StateBlock10(val state: Feat675UiModel, val checksum: Int)

fun buildFeat675UserItem(user: CoreUser, index: Int): Feat675UserItem1 {
    return Feat675UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat675StateBlock(model: Feat675UiModel): Feat675StateBlock1 {
    return Feat675StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat675UserSummary> {
    val list = java.util.ArrayList<Feat675UserSummary>(users.size)
    for (user in users) {
        list += Feat675UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat675UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat675UiModel {
    val summaries = (0 until count).map {
        Feat675UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat675UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat675UiModel> {
    val models = java.util.ArrayList<Feat675UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat675AnalyticsEvent1(val name: String, val value: String)
data class Feat675AnalyticsEvent2(val name: String, val value: String)
data class Feat675AnalyticsEvent3(val name: String, val value: String)
data class Feat675AnalyticsEvent4(val name: String, val value: String)
data class Feat675AnalyticsEvent5(val name: String, val value: String)
data class Feat675AnalyticsEvent6(val name: String, val value: String)
data class Feat675AnalyticsEvent7(val name: String, val value: String)
data class Feat675AnalyticsEvent8(val name: String, val value: String)
data class Feat675AnalyticsEvent9(val name: String, val value: String)
data class Feat675AnalyticsEvent10(val name: String, val value: String)

fun logFeat675Event1(event: Feat675AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat675Event2(event: Feat675AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat675Event3(event: Feat675AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat675Event4(event: Feat675AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat675Event5(event: Feat675AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat675Event6(event: Feat675AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat675Event7(event: Feat675AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat675Event8(event: Feat675AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat675Event9(event: Feat675AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat675Event10(event: Feat675AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat675Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat675Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat675(u: CoreUser): Feat675Projection1 =
    Feat675Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat675Projection1> {
    val list = java.util.ArrayList<Feat675Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat675(u)
    }
    return list
}
