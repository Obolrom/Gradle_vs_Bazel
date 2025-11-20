package com.romix.feature.feat614

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat614Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat614UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat614FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat614UserSummary
)

data class Feat614UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat614NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat614Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat614Config = Feat614Config()
) {

    fun loadSnapshot(userId: Long): Feat614NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat614NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat614UserSummary {
        return Feat614UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat614FeedItem> {
        val result = java.util.ArrayList<Feat614FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat614FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat614UiMapper {

    fun mapToUi(model: List<Feat614FeedItem>): Feat614UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat614UiModel(
            header = UiText("Feat614 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat614UiModel =
        Feat614UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat614UiModel =
        Feat614UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat614UiModel =
        Feat614UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat614Service(
    private val repository: Feat614Repository,
    private val uiMapper: Feat614UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat614UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat614UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat614UserItem1(val user: CoreUser, val label: String)
data class Feat614UserItem2(val user: CoreUser, val label: String)
data class Feat614UserItem3(val user: CoreUser, val label: String)
data class Feat614UserItem4(val user: CoreUser, val label: String)
data class Feat614UserItem5(val user: CoreUser, val label: String)
data class Feat614UserItem6(val user: CoreUser, val label: String)
data class Feat614UserItem7(val user: CoreUser, val label: String)
data class Feat614UserItem8(val user: CoreUser, val label: String)
data class Feat614UserItem9(val user: CoreUser, val label: String)
data class Feat614UserItem10(val user: CoreUser, val label: String)

data class Feat614StateBlock1(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock2(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock3(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock4(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock5(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock6(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock7(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock8(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock9(val state: Feat614UiModel, val checksum: Int)
data class Feat614StateBlock10(val state: Feat614UiModel, val checksum: Int)

fun buildFeat614UserItem(user: CoreUser, index: Int): Feat614UserItem1 {
    return Feat614UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat614StateBlock(model: Feat614UiModel): Feat614StateBlock1 {
    return Feat614StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat614UserSummary> {
    val list = java.util.ArrayList<Feat614UserSummary>(users.size)
    for (user in users) {
        list += Feat614UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat614UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat614UiModel {
    val summaries = (0 until count).map {
        Feat614UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat614UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat614UiModel> {
    val models = java.util.ArrayList<Feat614UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat614AnalyticsEvent1(val name: String, val value: String)
data class Feat614AnalyticsEvent2(val name: String, val value: String)
data class Feat614AnalyticsEvent3(val name: String, val value: String)
data class Feat614AnalyticsEvent4(val name: String, val value: String)
data class Feat614AnalyticsEvent5(val name: String, val value: String)
data class Feat614AnalyticsEvent6(val name: String, val value: String)
data class Feat614AnalyticsEvent7(val name: String, val value: String)
data class Feat614AnalyticsEvent8(val name: String, val value: String)
data class Feat614AnalyticsEvent9(val name: String, val value: String)
data class Feat614AnalyticsEvent10(val name: String, val value: String)

fun logFeat614Event1(event: Feat614AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat614Event2(event: Feat614AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat614Event3(event: Feat614AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat614Event4(event: Feat614AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat614Event5(event: Feat614AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat614Event6(event: Feat614AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat614Event7(event: Feat614AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat614Event8(event: Feat614AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat614Event9(event: Feat614AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat614Event10(event: Feat614AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat614Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat614Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat614(u: CoreUser): Feat614Projection1 =
    Feat614Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat614Projection1> {
    val list = java.util.ArrayList<Feat614Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat614(u)
    }
    return list
}
