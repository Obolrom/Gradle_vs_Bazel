package com.romix.feature.feat101

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat101Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat101UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat101FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat101UserSummary
)

data class Feat101UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat101NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat101Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat101Config = Feat101Config()
) {

    fun loadSnapshot(userId: Long): Feat101NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat101NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat101UserSummary {
        return Feat101UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat101FeedItem> {
        val result = java.util.ArrayList<Feat101FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat101FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat101UiMapper {

    fun mapToUi(model: List<Feat101FeedItem>): Feat101UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat101UiModel(
            header = UiText("Feat101 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat101UiModel =
        Feat101UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat101UiModel =
        Feat101UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat101UiModel =
        Feat101UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat101Service(
    private val repository: Feat101Repository,
    private val uiMapper: Feat101UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat101UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat101UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat101UserItem1(val user: CoreUser, val label: String)
data class Feat101UserItem2(val user: CoreUser, val label: String)
data class Feat101UserItem3(val user: CoreUser, val label: String)
data class Feat101UserItem4(val user: CoreUser, val label: String)
data class Feat101UserItem5(val user: CoreUser, val label: String)
data class Feat101UserItem6(val user: CoreUser, val label: String)
data class Feat101UserItem7(val user: CoreUser, val label: String)
data class Feat101UserItem8(val user: CoreUser, val label: String)
data class Feat101UserItem9(val user: CoreUser, val label: String)
data class Feat101UserItem10(val user: CoreUser, val label: String)

data class Feat101StateBlock1(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock2(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock3(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock4(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock5(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock6(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock7(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock8(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock9(val state: Feat101UiModel, val checksum: Int)
data class Feat101StateBlock10(val state: Feat101UiModel, val checksum: Int)

fun buildFeat101UserItem(user: CoreUser, index: Int): Feat101UserItem1 {
    return Feat101UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat101StateBlock(model: Feat101UiModel): Feat101StateBlock1 {
    return Feat101StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat101UserSummary> {
    val list = java.util.ArrayList<Feat101UserSummary>(users.size)
    for (user in users) {
        list += Feat101UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat101UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat101UiModel {
    val summaries = (0 until count).map {
        Feat101UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat101UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat101UiModel> {
    val models = java.util.ArrayList<Feat101UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat101AnalyticsEvent1(val name: String, val value: String)
data class Feat101AnalyticsEvent2(val name: String, val value: String)
data class Feat101AnalyticsEvent3(val name: String, val value: String)
data class Feat101AnalyticsEvent4(val name: String, val value: String)
data class Feat101AnalyticsEvent5(val name: String, val value: String)
data class Feat101AnalyticsEvent6(val name: String, val value: String)
data class Feat101AnalyticsEvent7(val name: String, val value: String)
data class Feat101AnalyticsEvent8(val name: String, val value: String)
data class Feat101AnalyticsEvent9(val name: String, val value: String)
data class Feat101AnalyticsEvent10(val name: String, val value: String)

fun logFeat101Event1(event: Feat101AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat101Event2(event: Feat101AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat101Event3(event: Feat101AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat101Event4(event: Feat101AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat101Event5(event: Feat101AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat101Event6(event: Feat101AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat101Event7(event: Feat101AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat101Event8(event: Feat101AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat101Event9(event: Feat101AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat101Event10(event: Feat101AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat101Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat101Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat101(u: CoreUser): Feat101Projection1 =
    Feat101Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat101Projection1> {
    val list = java.util.ArrayList<Feat101Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat101(u)
    }
    return list
}
