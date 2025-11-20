package com.romix.feature.feat205

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat205Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat205UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat205FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat205UserSummary
)

data class Feat205UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat205NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat205Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat205Config = Feat205Config()
) {

    fun loadSnapshot(userId: Long): Feat205NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat205NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat205UserSummary {
        return Feat205UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat205FeedItem> {
        val result = java.util.ArrayList<Feat205FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat205FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat205UiMapper {

    fun mapToUi(model: List<Feat205FeedItem>): Feat205UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat205UiModel(
            header = UiText("Feat205 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat205UiModel =
        Feat205UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat205UiModel =
        Feat205UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat205UiModel =
        Feat205UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat205Service(
    private val repository: Feat205Repository,
    private val uiMapper: Feat205UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat205UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat205UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat205UserItem1(val user: CoreUser, val label: String)
data class Feat205UserItem2(val user: CoreUser, val label: String)
data class Feat205UserItem3(val user: CoreUser, val label: String)
data class Feat205UserItem4(val user: CoreUser, val label: String)
data class Feat205UserItem5(val user: CoreUser, val label: String)
data class Feat205UserItem6(val user: CoreUser, val label: String)
data class Feat205UserItem7(val user: CoreUser, val label: String)
data class Feat205UserItem8(val user: CoreUser, val label: String)
data class Feat205UserItem9(val user: CoreUser, val label: String)
data class Feat205UserItem10(val user: CoreUser, val label: String)

data class Feat205StateBlock1(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock2(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock3(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock4(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock5(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock6(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock7(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock8(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock9(val state: Feat205UiModel, val checksum: Int)
data class Feat205StateBlock10(val state: Feat205UiModel, val checksum: Int)

fun buildFeat205UserItem(user: CoreUser, index: Int): Feat205UserItem1 {
    return Feat205UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat205StateBlock(model: Feat205UiModel): Feat205StateBlock1 {
    return Feat205StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat205UserSummary> {
    val list = java.util.ArrayList<Feat205UserSummary>(users.size)
    for (user in users) {
        list += Feat205UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat205UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat205UiModel {
    val summaries = (0 until count).map {
        Feat205UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat205UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat205UiModel> {
    val models = java.util.ArrayList<Feat205UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat205AnalyticsEvent1(val name: String, val value: String)
data class Feat205AnalyticsEvent2(val name: String, val value: String)
data class Feat205AnalyticsEvent3(val name: String, val value: String)
data class Feat205AnalyticsEvent4(val name: String, val value: String)
data class Feat205AnalyticsEvent5(val name: String, val value: String)
data class Feat205AnalyticsEvent6(val name: String, val value: String)
data class Feat205AnalyticsEvent7(val name: String, val value: String)
data class Feat205AnalyticsEvent8(val name: String, val value: String)
data class Feat205AnalyticsEvent9(val name: String, val value: String)
data class Feat205AnalyticsEvent10(val name: String, val value: String)

fun logFeat205Event1(event: Feat205AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat205Event2(event: Feat205AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat205Event3(event: Feat205AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat205Event4(event: Feat205AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat205Event5(event: Feat205AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat205Event6(event: Feat205AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat205Event7(event: Feat205AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat205Event8(event: Feat205AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat205Event9(event: Feat205AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat205Event10(event: Feat205AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat205Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat205Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat205(u: CoreUser): Feat205Projection1 =
    Feat205Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat205Projection1> {
    val list = java.util.ArrayList<Feat205Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat205(u)
    }
    return list
}
