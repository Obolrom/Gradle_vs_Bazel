package com.romix.feature.feat547

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat547Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat547UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat547FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat547UserSummary
)

data class Feat547UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat547NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat547Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat547Config = Feat547Config()
) {

    fun loadSnapshot(userId: Long): Feat547NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat547NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat547UserSummary {
        return Feat547UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat547FeedItem> {
        val result = java.util.ArrayList<Feat547FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat547FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat547UiMapper {

    fun mapToUi(model: List<Feat547FeedItem>): Feat547UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat547UiModel(
            header = UiText("Feat547 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat547UiModel =
        Feat547UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat547UiModel =
        Feat547UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat547UiModel =
        Feat547UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat547Service(
    private val repository: Feat547Repository,
    private val uiMapper: Feat547UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat547UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat547UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat547UserItem1(val user: CoreUser, val label: String)
data class Feat547UserItem2(val user: CoreUser, val label: String)
data class Feat547UserItem3(val user: CoreUser, val label: String)
data class Feat547UserItem4(val user: CoreUser, val label: String)
data class Feat547UserItem5(val user: CoreUser, val label: String)
data class Feat547UserItem6(val user: CoreUser, val label: String)
data class Feat547UserItem7(val user: CoreUser, val label: String)
data class Feat547UserItem8(val user: CoreUser, val label: String)
data class Feat547UserItem9(val user: CoreUser, val label: String)
data class Feat547UserItem10(val user: CoreUser, val label: String)

data class Feat547StateBlock1(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock2(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock3(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock4(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock5(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock6(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock7(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock8(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock9(val state: Feat547UiModel, val checksum: Int)
data class Feat547StateBlock10(val state: Feat547UiModel, val checksum: Int)

fun buildFeat547UserItem(user: CoreUser, index: Int): Feat547UserItem1 {
    return Feat547UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat547StateBlock(model: Feat547UiModel): Feat547StateBlock1 {
    return Feat547StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat547UserSummary> {
    val list = java.util.ArrayList<Feat547UserSummary>(users.size)
    for (user in users) {
        list += Feat547UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat547UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat547UiModel {
    val summaries = (0 until count).map {
        Feat547UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat547UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat547UiModel> {
    val models = java.util.ArrayList<Feat547UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat547AnalyticsEvent1(val name: String, val value: String)
data class Feat547AnalyticsEvent2(val name: String, val value: String)
data class Feat547AnalyticsEvent3(val name: String, val value: String)
data class Feat547AnalyticsEvent4(val name: String, val value: String)
data class Feat547AnalyticsEvent5(val name: String, val value: String)
data class Feat547AnalyticsEvent6(val name: String, val value: String)
data class Feat547AnalyticsEvent7(val name: String, val value: String)
data class Feat547AnalyticsEvent8(val name: String, val value: String)
data class Feat547AnalyticsEvent9(val name: String, val value: String)
data class Feat547AnalyticsEvent10(val name: String, val value: String)

fun logFeat547Event1(event: Feat547AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat547Event2(event: Feat547AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat547Event3(event: Feat547AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat547Event4(event: Feat547AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat547Event5(event: Feat547AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat547Event6(event: Feat547AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat547Event7(event: Feat547AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat547Event8(event: Feat547AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat547Event9(event: Feat547AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat547Event10(event: Feat547AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat547Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat547Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat547(u: CoreUser): Feat547Projection1 =
    Feat547Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat547Projection1> {
    val list = java.util.ArrayList<Feat547Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat547(u)
    }
    return list
}
