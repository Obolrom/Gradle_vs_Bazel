package com.romix.feature.feat29

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat29Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat29UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat29FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat29UserSummary
)

data class Feat29UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat29NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat29Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat29Config = Feat29Config()
) {

    fun loadSnapshot(userId: Long): Feat29NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat29NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat29UserSummary {
        return Feat29UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat29FeedItem> {
        val result = java.util.ArrayList<Feat29FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat29FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat29UiMapper {

    fun mapToUi(model: List<Feat29FeedItem>): Feat29UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat29UiModel(
            header = UiText("Feat29 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat29UiModel =
        Feat29UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat29UiModel =
        Feat29UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat29UiModel =
        Feat29UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat29Service(
    private val repository: Feat29Repository,
    private val uiMapper: Feat29UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat29UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat29UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat29UserItem1(val user: CoreUser, val label: String)
data class Feat29UserItem2(val user: CoreUser, val label: String)
data class Feat29UserItem3(val user: CoreUser, val label: String)
data class Feat29UserItem4(val user: CoreUser, val label: String)
data class Feat29UserItem5(val user: CoreUser, val label: String)
data class Feat29UserItem6(val user: CoreUser, val label: String)
data class Feat29UserItem7(val user: CoreUser, val label: String)
data class Feat29UserItem8(val user: CoreUser, val label: String)
data class Feat29UserItem9(val user: CoreUser, val label: String)
data class Feat29UserItem10(val user: CoreUser, val label: String)

data class Feat29StateBlock1(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock2(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock3(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock4(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock5(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock6(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock7(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock8(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock9(val state: Feat29UiModel, val checksum: Int)
data class Feat29StateBlock10(val state: Feat29UiModel, val checksum: Int)

fun buildFeat29UserItem(user: CoreUser, index: Int): Feat29UserItem1 {
    return Feat29UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat29StateBlock(model: Feat29UiModel): Feat29StateBlock1 {
    return Feat29StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat29UserSummary> {
    val list = java.util.ArrayList<Feat29UserSummary>(users.size)
    for (user in users) {
        list += Feat29UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat29UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat29UiModel {
    val summaries = (0 until count).map {
        Feat29UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat29UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat29UiModel> {
    val models = java.util.ArrayList<Feat29UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat29AnalyticsEvent1(val name: String, val value: String)
data class Feat29AnalyticsEvent2(val name: String, val value: String)
data class Feat29AnalyticsEvent3(val name: String, val value: String)
data class Feat29AnalyticsEvent4(val name: String, val value: String)
data class Feat29AnalyticsEvent5(val name: String, val value: String)
data class Feat29AnalyticsEvent6(val name: String, val value: String)
data class Feat29AnalyticsEvent7(val name: String, val value: String)
data class Feat29AnalyticsEvent8(val name: String, val value: String)
data class Feat29AnalyticsEvent9(val name: String, val value: String)
data class Feat29AnalyticsEvent10(val name: String, val value: String)

fun logFeat29Event1(event: Feat29AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat29Event2(event: Feat29AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat29Event3(event: Feat29AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat29Event4(event: Feat29AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat29Event5(event: Feat29AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat29Event6(event: Feat29AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat29Event7(event: Feat29AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat29Event8(event: Feat29AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat29Event9(event: Feat29AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat29Event10(event: Feat29AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat29Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat29Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat29(u: CoreUser): Feat29Projection1 =
    Feat29Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat29Projection1> {
    val list = java.util.ArrayList<Feat29Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat29(u)
    }
    return list
}
