package com.romix.feature.feat294

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat294Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat294UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat294FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat294UserSummary
)

data class Feat294UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat294NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat294Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat294Config = Feat294Config()
) {

    fun loadSnapshot(userId: Long): Feat294NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat294NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat294UserSummary {
        return Feat294UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat294FeedItem> {
        val result = java.util.ArrayList<Feat294FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat294FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat294UiMapper {

    fun mapToUi(model: List<Feat294FeedItem>): Feat294UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat294UiModel(
            header = UiText("Feat294 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat294UiModel =
        Feat294UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat294UiModel =
        Feat294UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat294UiModel =
        Feat294UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat294Service(
    private val repository: Feat294Repository,
    private val uiMapper: Feat294UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat294UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat294UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat294UserItem1(val user: CoreUser, val label: String)
data class Feat294UserItem2(val user: CoreUser, val label: String)
data class Feat294UserItem3(val user: CoreUser, val label: String)
data class Feat294UserItem4(val user: CoreUser, val label: String)
data class Feat294UserItem5(val user: CoreUser, val label: String)
data class Feat294UserItem6(val user: CoreUser, val label: String)
data class Feat294UserItem7(val user: CoreUser, val label: String)
data class Feat294UserItem8(val user: CoreUser, val label: String)
data class Feat294UserItem9(val user: CoreUser, val label: String)
data class Feat294UserItem10(val user: CoreUser, val label: String)

data class Feat294StateBlock1(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock2(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock3(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock4(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock5(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock6(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock7(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock8(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock9(val state: Feat294UiModel, val checksum: Int)
data class Feat294StateBlock10(val state: Feat294UiModel, val checksum: Int)

fun buildFeat294UserItem(user: CoreUser, index: Int): Feat294UserItem1 {
    return Feat294UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat294StateBlock(model: Feat294UiModel): Feat294StateBlock1 {
    return Feat294StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat294UserSummary> {
    val list = java.util.ArrayList<Feat294UserSummary>(users.size)
    for (user in users) {
        list += Feat294UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat294UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat294UiModel {
    val summaries = (0 until count).map {
        Feat294UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat294UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat294UiModel> {
    val models = java.util.ArrayList<Feat294UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat294AnalyticsEvent1(val name: String, val value: String)
data class Feat294AnalyticsEvent2(val name: String, val value: String)
data class Feat294AnalyticsEvent3(val name: String, val value: String)
data class Feat294AnalyticsEvent4(val name: String, val value: String)
data class Feat294AnalyticsEvent5(val name: String, val value: String)
data class Feat294AnalyticsEvent6(val name: String, val value: String)
data class Feat294AnalyticsEvent7(val name: String, val value: String)
data class Feat294AnalyticsEvent8(val name: String, val value: String)
data class Feat294AnalyticsEvent9(val name: String, val value: String)
data class Feat294AnalyticsEvent10(val name: String, val value: String)

fun logFeat294Event1(event: Feat294AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat294Event2(event: Feat294AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat294Event3(event: Feat294AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat294Event4(event: Feat294AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat294Event5(event: Feat294AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat294Event6(event: Feat294AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat294Event7(event: Feat294AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat294Event8(event: Feat294AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat294Event9(event: Feat294AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat294Event10(event: Feat294AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat294Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat294Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat294(u: CoreUser): Feat294Projection1 =
    Feat294Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat294Projection1> {
    val list = java.util.ArrayList<Feat294Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat294(u)
    }
    return list
}
