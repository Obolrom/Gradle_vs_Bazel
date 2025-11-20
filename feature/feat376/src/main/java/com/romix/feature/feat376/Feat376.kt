package com.romix.feature.feat376

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat376Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat376UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat376FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat376UserSummary
)

data class Feat376UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat376NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat376Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat376Config = Feat376Config()
) {

    fun loadSnapshot(userId: Long): Feat376NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat376NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat376UserSummary {
        return Feat376UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat376FeedItem> {
        val result = java.util.ArrayList<Feat376FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat376FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat376UiMapper {

    fun mapToUi(model: List<Feat376FeedItem>): Feat376UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat376UiModel(
            header = UiText("Feat376 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat376UiModel =
        Feat376UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat376UiModel =
        Feat376UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat376UiModel =
        Feat376UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat376Service(
    private val repository: Feat376Repository,
    private val uiMapper: Feat376UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat376UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat376UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat376UserItem1(val user: CoreUser, val label: String)
data class Feat376UserItem2(val user: CoreUser, val label: String)
data class Feat376UserItem3(val user: CoreUser, val label: String)
data class Feat376UserItem4(val user: CoreUser, val label: String)
data class Feat376UserItem5(val user: CoreUser, val label: String)
data class Feat376UserItem6(val user: CoreUser, val label: String)
data class Feat376UserItem7(val user: CoreUser, val label: String)
data class Feat376UserItem8(val user: CoreUser, val label: String)
data class Feat376UserItem9(val user: CoreUser, val label: String)
data class Feat376UserItem10(val user: CoreUser, val label: String)

data class Feat376StateBlock1(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock2(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock3(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock4(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock5(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock6(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock7(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock8(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock9(val state: Feat376UiModel, val checksum: Int)
data class Feat376StateBlock10(val state: Feat376UiModel, val checksum: Int)

fun buildFeat376UserItem(user: CoreUser, index: Int): Feat376UserItem1 {
    return Feat376UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat376StateBlock(model: Feat376UiModel): Feat376StateBlock1 {
    return Feat376StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat376UserSummary> {
    val list = java.util.ArrayList<Feat376UserSummary>(users.size)
    for (user in users) {
        list += Feat376UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat376UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat376UiModel {
    val summaries = (0 until count).map {
        Feat376UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat376UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat376UiModel> {
    val models = java.util.ArrayList<Feat376UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat376AnalyticsEvent1(val name: String, val value: String)
data class Feat376AnalyticsEvent2(val name: String, val value: String)
data class Feat376AnalyticsEvent3(val name: String, val value: String)
data class Feat376AnalyticsEvent4(val name: String, val value: String)
data class Feat376AnalyticsEvent5(val name: String, val value: String)
data class Feat376AnalyticsEvent6(val name: String, val value: String)
data class Feat376AnalyticsEvent7(val name: String, val value: String)
data class Feat376AnalyticsEvent8(val name: String, val value: String)
data class Feat376AnalyticsEvent9(val name: String, val value: String)
data class Feat376AnalyticsEvent10(val name: String, val value: String)

fun logFeat376Event1(event: Feat376AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat376Event2(event: Feat376AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat376Event3(event: Feat376AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat376Event4(event: Feat376AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat376Event5(event: Feat376AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat376Event6(event: Feat376AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat376Event7(event: Feat376AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat376Event8(event: Feat376AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat376Event9(event: Feat376AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat376Event10(event: Feat376AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat376Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat376Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat376(u: CoreUser): Feat376Projection1 =
    Feat376Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat376Projection1> {
    val list = java.util.ArrayList<Feat376Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat376(u)
    }
    return list
}
