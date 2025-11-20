package com.romix.feature.feat92

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat92Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat92UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat92FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat92UserSummary
)

data class Feat92UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat92NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat92Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat92Config = Feat92Config()
) {

    fun loadSnapshot(userId: Long): Feat92NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat92NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat92UserSummary {
        return Feat92UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat92FeedItem> {
        val result = java.util.ArrayList<Feat92FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat92FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat92UiMapper {

    fun mapToUi(model: List<Feat92FeedItem>): Feat92UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat92UiModel(
            header = UiText("Feat92 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat92UiModel =
        Feat92UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat92UiModel =
        Feat92UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat92UiModel =
        Feat92UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat92Service(
    private val repository: Feat92Repository,
    private val uiMapper: Feat92UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat92UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat92UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat92UserItem1(val user: CoreUser, val label: String)
data class Feat92UserItem2(val user: CoreUser, val label: String)
data class Feat92UserItem3(val user: CoreUser, val label: String)
data class Feat92UserItem4(val user: CoreUser, val label: String)
data class Feat92UserItem5(val user: CoreUser, val label: String)
data class Feat92UserItem6(val user: CoreUser, val label: String)
data class Feat92UserItem7(val user: CoreUser, val label: String)
data class Feat92UserItem8(val user: CoreUser, val label: String)
data class Feat92UserItem9(val user: CoreUser, val label: String)
data class Feat92UserItem10(val user: CoreUser, val label: String)

data class Feat92StateBlock1(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock2(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock3(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock4(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock5(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock6(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock7(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock8(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock9(val state: Feat92UiModel, val checksum: Int)
data class Feat92StateBlock10(val state: Feat92UiModel, val checksum: Int)

fun buildFeat92UserItem(user: CoreUser, index: Int): Feat92UserItem1 {
    return Feat92UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat92StateBlock(model: Feat92UiModel): Feat92StateBlock1 {
    return Feat92StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat92UserSummary> {
    val list = java.util.ArrayList<Feat92UserSummary>(users.size)
    for (user in users) {
        list += Feat92UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat92UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat92UiModel {
    val summaries = (0 until count).map {
        Feat92UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat92UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat92UiModel> {
    val models = java.util.ArrayList<Feat92UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat92AnalyticsEvent1(val name: String, val value: String)
data class Feat92AnalyticsEvent2(val name: String, val value: String)
data class Feat92AnalyticsEvent3(val name: String, val value: String)
data class Feat92AnalyticsEvent4(val name: String, val value: String)
data class Feat92AnalyticsEvent5(val name: String, val value: String)
data class Feat92AnalyticsEvent6(val name: String, val value: String)
data class Feat92AnalyticsEvent7(val name: String, val value: String)
data class Feat92AnalyticsEvent8(val name: String, val value: String)
data class Feat92AnalyticsEvent9(val name: String, val value: String)
data class Feat92AnalyticsEvent10(val name: String, val value: String)

fun logFeat92Event1(event: Feat92AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat92Event2(event: Feat92AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat92Event3(event: Feat92AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat92Event4(event: Feat92AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat92Event5(event: Feat92AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat92Event6(event: Feat92AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat92Event7(event: Feat92AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat92Event8(event: Feat92AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat92Event9(event: Feat92AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat92Event10(event: Feat92AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat92Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat92Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat92(u: CoreUser): Feat92Projection1 =
    Feat92Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat92Projection1> {
    val list = java.util.ArrayList<Feat92Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat92(u)
    }
    return list
}
