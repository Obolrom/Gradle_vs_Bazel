package com.romix.feature.feat228

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat228Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat228UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat228FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat228UserSummary
)

data class Feat228UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat228NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat228Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat228Config = Feat228Config()
) {

    fun loadSnapshot(userId: Long): Feat228NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat228NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat228UserSummary {
        return Feat228UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat228FeedItem> {
        val result = java.util.ArrayList<Feat228FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat228FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat228UiMapper {

    fun mapToUi(model: List<Feat228FeedItem>): Feat228UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat228UiModel(
            header = UiText("Feat228 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat228UiModel =
        Feat228UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat228UiModel =
        Feat228UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat228UiModel =
        Feat228UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat228Service(
    private val repository: Feat228Repository,
    private val uiMapper: Feat228UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat228UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat228UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat228UserItem1(val user: CoreUser, val label: String)
data class Feat228UserItem2(val user: CoreUser, val label: String)
data class Feat228UserItem3(val user: CoreUser, val label: String)
data class Feat228UserItem4(val user: CoreUser, val label: String)
data class Feat228UserItem5(val user: CoreUser, val label: String)
data class Feat228UserItem6(val user: CoreUser, val label: String)
data class Feat228UserItem7(val user: CoreUser, val label: String)
data class Feat228UserItem8(val user: CoreUser, val label: String)
data class Feat228UserItem9(val user: CoreUser, val label: String)
data class Feat228UserItem10(val user: CoreUser, val label: String)

data class Feat228StateBlock1(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock2(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock3(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock4(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock5(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock6(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock7(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock8(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock9(val state: Feat228UiModel, val checksum: Int)
data class Feat228StateBlock10(val state: Feat228UiModel, val checksum: Int)

fun buildFeat228UserItem(user: CoreUser, index: Int): Feat228UserItem1 {
    return Feat228UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat228StateBlock(model: Feat228UiModel): Feat228StateBlock1 {
    return Feat228StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat228UserSummary> {
    val list = java.util.ArrayList<Feat228UserSummary>(users.size)
    for (user in users) {
        list += Feat228UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat228UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat228UiModel {
    val summaries = (0 until count).map {
        Feat228UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat228UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat228UiModel> {
    val models = java.util.ArrayList<Feat228UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat228AnalyticsEvent1(val name: String, val value: String)
data class Feat228AnalyticsEvent2(val name: String, val value: String)
data class Feat228AnalyticsEvent3(val name: String, val value: String)
data class Feat228AnalyticsEvent4(val name: String, val value: String)
data class Feat228AnalyticsEvent5(val name: String, val value: String)
data class Feat228AnalyticsEvent6(val name: String, val value: String)
data class Feat228AnalyticsEvent7(val name: String, val value: String)
data class Feat228AnalyticsEvent8(val name: String, val value: String)
data class Feat228AnalyticsEvent9(val name: String, val value: String)
data class Feat228AnalyticsEvent10(val name: String, val value: String)

fun logFeat228Event1(event: Feat228AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat228Event2(event: Feat228AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat228Event3(event: Feat228AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat228Event4(event: Feat228AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat228Event5(event: Feat228AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat228Event6(event: Feat228AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat228Event7(event: Feat228AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat228Event8(event: Feat228AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat228Event9(event: Feat228AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat228Event10(event: Feat228AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat228Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat228Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat228(u: CoreUser): Feat228Projection1 =
    Feat228Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat228Projection1> {
    val list = java.util.ArrayList<Feat228Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat228(u)
    }
    return list
}
