package com.romix.feature.feat155

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat155Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat155UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat155FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat155UserSummary
)

data class Feat155UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat155NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat155Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat155Config = Feat155Config()
) {

    fun loadSnapshot(userId: Long): Feat155NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat155NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat155UserSummary {
        return Feat155UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat155FeedItem> {
        val result = java.util.ArrayList<Feat155FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat155FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat155UiMapper {

    fun mapToUi(model: List<Feat155FeedItem>): Feat155UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat155UiModel(
            header = UiText("Feat155 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat155UiModel =
        Feat155UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat155UiModel =
        Feat155UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat155UiModel =
        Feat155UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat155Service(
    private val repository: Feat155Repository,
    private val uiMapper: Feat155UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat155UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat155UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat155UserItem1(val user: CoreUser, val label: String)
data class Feat155UserItem2(val user: CoreUser, val label: String)
data class Feat155UserItem3(val user: CoreUser, val label: String)
data class Feat155UserItem4(val user: CoreUser, val label: String)
data class Feat155UserItem5(val user: CoreUser, val label: String)
data class Feat155UserItem6(val user: CoreUser, val label: String)
data class Feat155UserItem7(val user: CoreUser, val label: String)
data class Feat155UserItem8(val user: CoreUser, val label: String)
data class Feat155UserItem9(val user: CoreUser, val label: String)
data class Feat155UserItem10(val user: CoreUser, val label: String)

data class Feat155StateBlock1(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock2(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock3(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock4(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock5(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock6(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock7(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock8(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock9(val state: Feat155UiModel, val checksum: Int)
data class Feat155StateBlock10(val state: Feat155UiModel, val checksum: Int)

fun buildFeat155UserItem(user: CoreUser, index: Int): Feat155UserItem1 {
    return Feat155UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat155StateBlock(model: Feat155UiModel): Feat155StateBlock1 {
    return Feat155StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat155UserSummary> {
    val list = java.util.ArrayList<Feat155UserSummary>(users.size)
    for (user in users) {
        list += Feat155UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat155UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat155UiModel {
    val summaries = (0 until count).map {
        Feat155UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat155UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat155UiModel> {
    val models = java.util.ArrayList<Feat155UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat155AnalyticsEvent1(val name: String, val value: String)
data class Feat155AnalyticsEvent2(val name: String, val value: String)
data class Feat155AnalyticsEvent3(val name: String, val value: String)
data class Feat155AnalyticsEvent4(val name: String, val value: String)
data class Feat155AnalyticsEvent5(val name: String, val value: String)
data class Feat155AnalyticsEvent6(val name: String, val value: String)
data class Feat155AnalyticsEvent7(val name: String, val value: String)
data class Feat155AnalyticsEvent8(val name: String, val value: String)
data class Feat155AnalyticsEvent9(val name: String, val value: String)
data class Feat155AnalyticsEvent10(val name: String, val value: String)

fun logFeat155Event1(event: Feat155AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat155Event2(event: Feat155AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat155Event3(event: Feat155AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat155Event4(event: Feat155AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat155Event5(event: Feat155AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat155Event6(event: Feat155AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat155Event7(event: Feat155AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat155Event8(event: Feat155AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat155Event9(event: Feat155AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat155Event10(event: Feat155AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat155Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat155Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat155(u: CoreUser): Feat155Projection1 =
    Feat155Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat155Projection1> {
    val list = java.util.ArrayList<Feat155Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat155(u)
    }
    return list
}
